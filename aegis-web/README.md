##项目简介

      1、本项目是基于 vue + vue-router + iview + axios + echarts + vuex开发的

      2、项目的入口 src/main.js

      3、本项目是对orcale数据库的表空间、段空间、sql区段、逻辑读、逻辑写、活动会话历史等的一个监控项目

##前端代码打包

      1、项目地址 https://github.com/mingheinfo/aegis_web.git

      2、在package.json目录下运行yarn install 或者npm install 或者cnpm install

      3、本地调试在vue.config.js中修改devServer.proxy的target为服务器地址

      4、运行yarn run dev 或者npm run dev 或者cnpm run dev

      5、在浏览器中运行 http://localhost:9999

###生产环境

      1、运行npm run build 生成dist文件夹下的路径

      2、部署将dist文件夹下的文件拷贝到后端的src\main\resources\static下 （后端地址 https://github.com/mingheinfo/aegis.git）

      3、运行后端打包成jar包

      4、上传到后端服务器运行 nohup java -jar aegis-1.0.jar > aegis.log &

      5、打开页面地址 http://10.10.10.46:8888/aegis/login

      6、登录