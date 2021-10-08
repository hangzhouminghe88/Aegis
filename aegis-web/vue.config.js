const CompressionPlugin = require('compression-webpack-plugin');
const LodashModuleReplacementPlugin = require('lodash-webpack-plugin');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const path = require('path');
const webpack = require('webpack');
const resolve = (dir) => {
    return path.join(__dirname, './', dir);
}
module.exports = {
    devServer: {
        host: '127.0.0.1',
        port: 9999,
        proxy: {
            '/api': {
                target: 'http://127.0.0.1:8888',  // 请求本地 需要xboot后台项目
                ws: true
            },
            '~*.txt': {
                target: 'http://199.120.75.100:80'
            },
            // '/api/ws': {
            //     target: 'ws://localhost:8080/',
            //     ws: true,
            //     logLevel: 'debug',
            // }
        }
    },
    // 打包时不生成.map文件 避免看到源码
    productionSourceMap: false,
    configureWebpack: {
        module: {
            rules: [{
                test: /\.(js)$/,
                loader: 'babel-loader',
                exclude: /node_modules/,
                include: [resolve('src'), resolve('node_modules/iview')],
                options: { plugins: ['lodash'] }
            }
                //   {
                //   test: /\.less$/,
                //   use: [
                //     MiniCssExtractPlugin.loader,
                //     {
                //       loader: 'css-loader',
                //       options: {
                //         importLoaders: 2,
                //       }
                //     },
                //     {
                //       loader: "less-loader",
                //       options: {
                //         javascriptEnabled: true
                //       }
                //     }
                //     // 'less-loader',
                //   ]
                // }
            ]
        },
        resolve: {
            alias: {
                'src': resolve('src'),
                'assets': resolve('src/assets'),
                '@views': resolve('src/views'),
                '@api': resolve('src/api'),
                '@styles': resolve('src/styles')
            },
            extensions: ['.js', '.vue', '.css', '.json']
        },
        optimization: {
            usedExports: true,
            runtimeChunk: {
                name: 'runtime'
            },
            splitChunks: {
                chunks: "async", // 共有三个值可选：initial(初始模块)、async(按需加载模块)和all(全部模块)
                minSize: 30000, // 模块超过30k自动被抽离成公共模块
                minChunks: 1, // 模块被引用>=1次，便分割
                name: true, // 默认由模块名+hash命名，名称相同时多个模块将合并为1个，可以设置为function
                automaticNameDelimiter: '~', // 命名分隔符
                cacheGroups: {
                    default: { // 模块缓存规则，设置为false，默认缓存组将禁用
                        minChunks: 2, // 模块被引用>=2次，拆分至vendors公共模块
                        priority: -20, // 优先级
                        reuseExistingChunk: true, // 默认使用已有的模块
                    },
                    vendor: {
                        test: /[\\/]node_modules[\\/]/,
                        name: 'vendor',
                        // minChunks: 1,
                        priority: -10,// 确定模块打入的优先级
                        reuseExistingChunk: true,// 使用复用已经存在的模块
                        enforce: true,
                    },
                    echarts: {
                        test: /[\\/]node_modules[\\/]echarts/,
                        name: 'echarts',
                        priority: 16,
                        reuseExistingChunk: true,
                    }
                },
            },
        },
        plugins: [
            new CompressionPlugin({
                test: /\.js$|\.html$|\.css/, // 匹配文件
                threshold: 10240 // 对超过10k文件压缩
            }),
            new webpack.optimize.LimitChunkCountPlugin({
                maxChunks: 10,
                minChunkSize: 1000
            }),
            new LodashModuleReplacementPlugin()
        ]
    },
    css: { // 配置css模块
        loaderOptions: { // 向预处理器 Loader 传递配置选项
            less: { // 配置less（其他样式解析用法一致）
                javascriptEnabled: true // 设置为true
            }
        }
    },
    chainWebpack: config => {
        const svgRule = config.module.rule('svg');
        // 清除已有的所有 loader。
        // 如果你不这样做，接下来的 loader 会附加在该规则现有的 loader 之后。
        svgRule.uses.clear();
        svgRule
            .test(/\.svg$/)
            .include
            .add(resolve("src/assets"))
            .add(resolve("node_modules/view-design"))
            .add(resolve("src/views"))
            .end()
            .use('url-loader')
            .loader('url-loader')
            .options({
                esModule: false,
                fallback: {
                    loader: 'file-loader'
                }
            })
    }
}
