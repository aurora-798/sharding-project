<template>
  <div class="container">
    <div class="dig">
      <el-dialog v-model="dialogFormVisible" title="文件上传" width="500">
        <form @submit.prevent="uploadFile"  method="post" enctype="multipart/form-data">
          <div>
            <input type="file" name="file" ref="fileInput">
          </div>
          <div>
            选择分类:
            <select name="category" v-model="selectedCategory">
              <option :value="index + 1" v-for="(item,index) in categoryList" :key="index">{{item}}</option>
            </select>
          </div>
          <div>
            <button type="submit">上传</button>
          </div>
        </form>
      </el-dialog>
    </div>

    <div class="delDig">
      <el-dialog
          v-model="dialogDelVisible"
          title="删除文件"
          width="500"
      >
        <span>确定要删除吗？删除操作不可逆!</span>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="DeleteCanel">取消</el-button>
            <el-button type="primary" @click="CalcDelete">
              确定
            </el-button>
          </div>
        </template>
      </el-dialog>
    </div>

    <div class="loginDig">
      <el-dialog
          v-model="LogindialogVisible"
          title="登录"
          width="500"
      >
        <div>
          <el-form
              label-position="right"
              label-width="auto"
              :model="formLabelAlign"
              style="max-width: 600px"
          >
            <el-form-item label="email">
              <el-input v-model="formLabelAlign.email" placeholder="xxxxx@qq.com" type="text"/>
            </el-form-item>
            <el-form-item label="password">
              <el-input v-model="formLabelAlign.password" placeholder="***" type="password"/>
            </el-form-item>
          </el-form>
        </div>
        <template #footer>
          <div class="dialog-footer">
            <div class="login-btn">
              <el-button @click="LogindialogVisible = false">取消</el-button>
              <el-button type="primary" @click="Login">
                登录
              </el-button>
            </div>
          </div>
        </template>
      </el-dialog>
    </div>
    <div class="header">
      <h1>文件下载系统</h1>
      <div v-if="!UserInfo">
        <el-button @click="LogindialogVisible = true">登录</el-button>
        <el-button @click="hrefChanggeWebSite">注册</el-button>
      </div>
      <div v-else>
        欢迎你,{{UserInfo.nickName}}
        <el-button @click="exit" class="exit">退出</el-button>
        <el-button @click="hrefChanggeWebSite" class="exit">主站</el-button>
      </div>
    </div>
    <button v-if="UserInfo != null" @click="handleUpload">上传文件</button>
    <div v-for="category in categories" :key="category.name" class="category">
      <div class="category-title">
        {{ category.categoryName }}
      </div>
      <ul class="file-list">
        <li v-for="file in category.fileList" :key="file.categoryId" class="file-item">
          {{ DownLoadToken }}
          <a @click="handleDownload($event)"
             :href="download + file.fileName + '?token=' +
((UserInfo == null) ? '' : UserInfo.token)" download>{{ file.fileName }}</a>
          <span class="size">文件大小：{{ file.size }}{{ file.suffix }}</span>
          <span class="count">下载次数： {{ file.downloadCount }}</span>
          <span class="update">更新时间：{{ file.updateTime }}</span>
          <el-button v-if="UserInfo != null" @click="del(file.fileName)" type="danger" :icon="Delete" circle></el-button>
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import {onMounted, ref, reactive, watch} from 'vue';
import axios from 'axios';
import { ElMessage } from 'element-plus'
import { Delete } from '@element-plus/icons-vue'
import { ElLoading } from 'element-plus'
let SparkMD5 = require('spark-md5')
// slice是按照字节来切片的，而且file.size获取的也是字节。我们这里把每一片切片大小定义为1M，1M = 1024 * 1024B(字节)
const CUT_SIZE = 1024 * 1024
//文件名
const fileName = ref('')
//文件哈希
const fileHash = ref('')
const UserInfo = ref(null);
const download = ref("http://101.42.240.114:9999/file/download/");
const fileSize = ref(0)
const selectedCategory = ref('');
const fileInput = ref(null);

const dialogFormVisible = ref(false);
const dialogDelVisible = ref(false);
const LogindialogVisible = ref(false);

const formLabelAlign = reactive({
  email: '',
  password: ''
});

const DeleteName = ref(null);
const categoryList = ref([]);
const categories = ref(null);



// 文件上传
const upload = async (cut) => {
  // 1. 将cut数组封装为formData对象，便于上传
  const data = cut.map((cut,index) => {
        return {
          fileHash: fileHash.value,
          cutHash: fileHash.value + '-' + index,
          cut
        }
      }
  )
  // 将data对象封装为formData对象
  const formDates = data.map((item) => {
    const form = new FormData()
    form.append('fileHash',item.fileHash)
    form.append('cutHash',item.cutHash)
    form.append('cut',item.cut)
    return form
  })

  //2. 准备发送请求
  // 浏览器同一时刻发送请求数量
  const max = 8
  let index = 0
  const taskPool = []
  while (index < formDates.length) {
    const task = fetch('http://101.42.240.114:9999/file/upload', {
      method: 'POST',
      headers: {
        'token': UserInfo.value.token
      },
      body: formDates[index]
    }).finally(() => {
      // 请求完成后立即从任务池中移除
      const taskIndex = taskPool.indexOf(task)
      if (taskIndex > -1) {
        taskPool.splice(taskIndex, 1)
      }
    })
    taskPool.push(task)
    // 如果任务池中的请求数量达到最大并发数，则等待其中一个完成
    if (taskPool.length >= max) {
      await Promise.race(taskPool)
    }
    index += 1
  }
// 确保所有剩余的请求完成
  await Promise.all(taskPool)
  const mergeInfo = {
    cutSize: formDates.length,
    fileName: fileName.value,
    fileHash: fileHash.value,
    category: selectedCategory.value,
    fileSize: fileSize.value
  }

// 发送合并请求
  fetch('http://101.42.240.114:9999/file/merge', {
    headers: {
      'Content-Type': 'application/json',
      'token': UserInfo.value.token
    },
    method: 'POST',
    body: JSON.stringify(mergeInfo)
  }).then(() => {
    loadingInstance.value.close()
    getCategoryList(); // 上传成功后重新获取文件列表
    ElMessage({
      message: '上传成功',
      type: 'success',
    });
    dialogFormVisible.value = false; // 关闭上传对话框
  })
}

onMounted(() => {
  getCategoryList();
  UserInfo.value = JSON.parse(localStorage.getItem("loginInfo"));
  axios.get('http://101.42.240.114:9999/category/list', null).then(response => {
    categoryList.value = response.data.data;
  }, error => {
    console.log('错误', error.message);
  });

  // 监听 UserInfo 的变化并更新 DownLoadToken
  watch(UserInfo, (value, oldValue) => {
    console.log(value, oldValue);
    UserInfo.value = value
  });
});

// 文件切片
// 文件切片
const fileCut = (file) => {
  //定义一个指针用于指向当前要读取大文件的起始位置
  let cur = 0
  // 保存每一次的切片数据
  let cut = []

  while (cur < file.size) {
    const data = file.slice(cur,cur + CUT_SIZE)
    cut.push(data)
    cur += CUT_SIZE
  }
  return cut;
}

// 计算哈希
const calcFileHash = (cuts) => {
  // 由于我们需要立即得到文件的哈希值，但下面计算哈希值是异步执行的，我们需要使用返回一个Promise同步
  return new Promise(resolve => {
    const targets = []
    cuts.forEach((cut,index) => {
          // 前后两个切片文件全部参与计算
          if (index == 0 || index == cuts.length - 1)
            targets.push(cut.file)
          // 其余每一个切片文件取前面、中间、后面各两个字节来参与计算
          else {
            targets.push(cut.slice(0,2))
            targets.push(cut.slice(CUT_SIZE / 2,CUT_SIZE / 2 + 2))
            targets.push(cut.slice(CUT_SIZE - 2,CUT_SIZE))
          }
        }
    )
    let spark = new SparkMD5.ArrayBuffer();
    let fileReader = new FileReader(); //读取文件
    fileReader.readAsArrayBuffer(new Blob(cuts))
    fileReader.onload = ((e) => {
      spark.append(e.target.result)
      resolve(spark.end())
    })
  })
}



/**
 * 上传文件
 */
const loadingInstance = ref(null)
const uploadFile = async () => {
  const files = fileInput.value.files;
  if (!files) return
  if (files[0] === undefined) {
    return
  }
  fileSize.value = files[0].size
  fileName.value = files[0].name
  // loading
  loadingInstance.value =
      ElLoading.service({text: '文件上传中，请稍等...',lock: 'true',background: 'rgba(0, 0, 0, 0.7)'})

  const cut = fileCut(files[0])
  const hash = await calcFileHash(cut)
  fileHash.value = hash
  upload(cut)
};


const handleDownload = (event) => {
  if (UserInfo.value == null) {
    event.preventDefault();
    ElMessage({
      message: '请登录',
      type: 'warning',
    });
    LogindialogVisible.value = true;
  }
};

const handleUpload = () => {
  if (UserInfo.value == null) {
    ElMessage({
      message: '请登录',
      type: 'warning',
    });
    LogindialogVisible.value = true;
    return;
  }
  dialogFormVisible.value = true;
};

const handleDel = (fileName) => {
  if (UserInfo.value == null) {
    ElMessage({
      message: '请登录',
      type: 'warning',
    });
    LogindialogVisible.value = true;
    return;
  }
  dialogDelVisible.value = true;
  DeleteName.value = fileName;
};

const getCategoryList = () => {
  axios.post('http://101.42.240.114:9999/file/list', null).then(response => {
    categories.value = response.data.data;
  }, error => {
    console.log('错误', error.message);
  });
};

const Login = () => {
  LogindialogVisible.value = false;
  axios.post('http://101.42.240.114:9999/user/login', JSON.stringify(formLabelAlign), {
    headers: {
      'Content-Type': 'application/json'
    }
  }).then((res) => {
    if (res.data.code == 401) {
      ElMessage({
        message: res.data.msg,
        type: 'warning',
      });
    } else if (res.data.code == 402) {
      window.location.href = "http://changge.plus";
      ElMessage({
        message: res.data.msg,
        type: 'warning',
      });
    } else {
      localStorage.setItem("loginInfo", JSON.stringify(res.data.data));
      UserInfo.value = res.data.data;
      ElMessage({
        message: '登录成功',
        type: 'success',
      });
      formLabelAlign.email = "";
      formLabelAlign.password = "";
    }
  });
};

const exit = () => {
  localStorage.removeItem("loginInfo");
  UserInfo.value = null;
};


const DeleteFile = (fileName) => {
  const obj = {
    fileName,
    token: UserInfo.value.token
  }
  axios.post('http://101.42.240.114:9999/file/del', JSON.stringify(obj), {
    headers: {
      'Content-Type': 'application/json',
      'token': UserInfo.value.token
    }
  }).then((res) => {
    if (res.data.data.code == 401) {
      ElMessage({
        message: res.data.data.msg,
        type: 'error',
      });
      return
    }
    getCategoryList();
    ElMessage({
      message: res.data.data.data,
      type: 'success',
    });
  }, error => {
    console.log('错误', error.message);
  });
};

const CalcDelete = () => {
  DeleteFile(DeleteName.value);

  dialogDelVisible.value = false;
  DeleteName.value = null;
};

const del = (fileName) => {
  handleDel(fileName);
};

const DeleteCanel = () => {
  dialogDelVisible.value = false;
  DeleteName.value = null;
};

const hrefChanggeWebSite = () => {
  window.location.href = "http://changge.plus";
};

</script>

<style scoped>

.dig  div{
  margin: 20px;
}

.container {
  max-width: 1200px;
  width: 100%;
  padding: 20px;
}
h1 {
  text-align: center;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
}
.form-group input,
.form-group select {
  width: 100%;
  padding: 8px;
  box-sizing: border-box;
}
button {
  padding: 10px 20px;
  background-color: #007bff;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
button:hover {
  background-color: #0056b3;
}
.category {
  margin: 30px 0px;
}
.category-title {
  font-size: 30px;
  margin-bottom: 20px;
  font-weight: bolder;
}
.file-list {
  list-style-type: none;
  padding: 0;
  margin: 0;
}
.file-item {
  padding: 10px;
  margin: 10px 0;
  border-bottom: 1px solid #ddd;
  border-top: 1px solid #ddd;
  border-radius: 4px;

  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}


.file-item a {
  text-decoration: none;
  color: #007bff;
}


.file-item a,
.file-item .size,
.file-item .count,
.file-item .update {
  flex: 1;
  text-align: center; /* 让文本在元素内部居中 */
  padding: 5px;
  box-sizing: border-box;
}


.file-item a:hover {
  text-decoration: underline; /* 添加悬停效果 */
}

.file-item .size,
.file-item .count,
.file-item .update {
  color: #333; /* 文本颜色，可以根据需要调整 */
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.login-btn {
  display: flex;
  align-items: center;
  justify-content: space-around;
}


.exit {
  margin-left: 27px;
}

@media (max-width: 768px) {
  .category-title {
    font-size: 20px;
  }
}
@media (max-width: 480px) {
  .category-title {
    font-size: 18px;
  }
}
</style>
