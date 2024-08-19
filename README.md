# README
项目做完已经好久了但是最近才上传，代码写的什么我也几乎忘记的差不多了。分片的逻辑主要是front实现，后端来配合前端来实现分片逻辑。
不过我依稀记得本项目的代码思路：
1. 前端通过把一个大的文件分成若干个小文件。
2. 通过浏览器的并发能力(同一时刻可以处理多个请求)来在同一时刻来发送多个请求(向后端发送小文件)。
3. 后端接收请求并处理多个小文件，讲小文件保存到磁盘中。
4. 前端发送合并请求，表示小文件已经上传完成，可以进行合并。
5. 后端进行合并文件处理。
6. 清理所有的小文件。
   
