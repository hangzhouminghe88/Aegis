function formatDate(date, fmt) {
  if (/(y+)/.test(fmt)) {
    fmt = fmt.replace(RegExp.$1, (date.getFullYear() + '').substr(4 - RegExp.$1.length))
  }
  let o = {
    'M+': date.getMonth() + 1,
    'd+': date.getDate(),
    'h+': date.getHours(),
    'm+': date.getMinutes(),
    's+': date.getSeconds()
  }
  for (let k in o) {
    if (new RegExp(`(${k})`).test(fmt)) {
      let str = o[k] + ''
      fmt = fmt.replace(RegExp.$1, RegExp.$1.length === 1 ? str : padLeftZero(str))
    }
  }
  return fmt
}

function padLeftZero(str) {
  return ('00' + str).substr(str.length)
}

 function parseDate(dt) {
  if (dt) {
    return new Date(Date.parse(dt.replace(/([-])/g, '/').replace(/([|+|T])/g, ' ').split('.')[0]));
  }
}
//格式化size大小
const formatSize = (bytes, unit, width) => {
  bytes = Number(bytes);
  // if (bytes < 1 && bytes > 0)
  if (typeof bytes !== 'number' || isNaN(bytes)) bytes = 0;
  if (bytes < 0) bytes = 0;
  if (typeof width === 'undefined') width = 2;
  if (typeof unit === 'undefined') unit = '';
  var num = Math.pow(10, width);
  var sizes = ['K', 'M', 'G', 'T', 'P'];
  if (unit) {
    sizes.unshift('')
  } else {
    sizes.unshift('')
  }
  if (bytes === 0) return '0 ' + sizes[0] + unit;
  var i = Math.floor(Math.log(bytes) / Math.log(1024));
  // for 0.xxxx number
  if (i < 0) i = 0;
  if (sizes[i] === 'B') num = 1;
  if (i >= 5) i = 5;
  return Math.round(bytes / Math.pow(1024, i) * num) / num + ' ' + sizes[i] + unit
}
//格式化计数大小
const formatCount = (bytes, unit, width) => {
  bytes = Number(bytes);
  // if (bytes < 1 && bytes > 0)
  if (typeof bytes !== 'number' || isNaN(bytes)) bytes = 0;
  if (bytes < 0) bytes = 0;
  if (typeof width === 'undefined') width = 2;
  if (typeof unit === 'undefined') unit = '';
  var num = Math.pow(10, width);
  var sizes = ['k', 'm'];
  if (unit) {
    sizes.unshift('')
  } else {
    sizes.unshift('')
  }
  if (bytes === 0) return '0 ' + sizes[0] + unit;
  var i = Math.floor(Math.log(bytes) / Math.log(1000));
  // for 0.xxxx number
  if (i < 0) i = 0;
  if (sizes[i] === 'b') num = 1;
  if (i >= 5) i = 5;
  return Math.round(bytes / Math.pow(1000, i) * num) / num + ' ' + sizes[i] + unit
}
//格式化毫秒数
const formatMilliSecond = (bytes, unit, width) => {
  bytes = Number(bytes);
  // if (bytes < 1 && bytes > 0)
  if (typeof bytes !== 'number' || isNaN(bytes)) bytes = 0;
  if (bytes < 0) bytes = 0;
  if (typeof width === 'undefined') width = 2;
  if (typeof unit === 'undefined') unit = '';
  var num = Math.pow(10, width);
  var sizes = ['m', ''];
  if (unit) {
    sizes.unshift('')
  } else {
    sizes.unshift('')
  }
  if (bytes === 0) return '0 ' + sizes[0] + unit;
  var i = Math.floor(Math.log(bytes) / Math.log(1000));
  // for 0.xxxx number
  if (i < 0) i = 0;
  if (sizes[i] === 'u') num = 1;
  if (i >= 5) i = 5;
  return Math.round(bytes / Math.pow(1000, i) * num) / num + ' ' + sizes[i] + (unit ? unit : 's')
}

//阻止事件冒泡
const stopBubble = (e) => {
  let ev = e || window.event;
  //如果提供了事件对象，则这是一个非IE浏览器
  if (ev && ev.stopPropagation) {
    //因此它支持W3C的stopPropagation()方法
    ev.stopPropagation();
  } else {
    //否则，我们需要使用IE的方式来取消事件冒泡
    ev.cancelBubble = true;
  }
}
//保留4位有效数字如果不为数字返回为0
const formatRateTo4 = (data) => {
  if (!data || !/^\d\.[0-9]+$/.test(data)) {
    return 0;
  }
  return (data * 100).toPrecision(4);
}

//复制文本数据调用copy命令得到复制文本
function copyText(content) {
  let targetElm;
  if (content.indexOf('\n') !== -1) {
    targetElm = document.createElement('textarea')
  } else {
    targetElm = document.createElement('span')
  }
  var newContent = document.createTextNode(content);
  targetElm.appendChild(newContent);
  document.body.appendChild(targetElm);
  Promise.resolve().then(() => {
    var range = document.createRange();
    range.selectNode(targetElm);
    window.getSelection().removeAllRanges();
    Promise.resolve().then(() => {
      // var range = document.createRange()
      range.selectNode(targetElm);
      window.getSelection().addRange(range);
      //执行copy命令
      document.execCommand('copy');
      window.getSelection().removeAllRanges();
      document.body.removeChild(targetElm)
    })
  })
}

//导出cvs文件
const exportData = (metaType, content, name) => {
  if (!content) return;
  let type = metaType ? metaType : 'text/plain';
  let blob = new window.Blob(["\ufeff" + content], {type: 'text/csv,charset=UTF-8'});
  let link = document.createElement('a');
  link.setAttribute('display', 'none');
  link.download = name;
  link.href = window.URL.createObjectURL(blob);
  document.body.appendChild(link);
  link.click();
  setTimeout(() => {
    document.body.removeChild(link);
  }, 0)
}

const dateFormatToStamp = function (param) {
  if (!param) return;
  return param * 1000;
}

//防抖
const debounce = (fn, time, immediate) => {
  let timeOutID = null;
  return function () {
    let context = this;
    let args = arguments;
    if (timeOutID) clearTimeout(timeOutID);
    if (immediate) {
      let callNow = !timeOutID;
      timeOutID = setTimeout(() => {
        timeOutID = null;
      }, time)
      if (callNow) fn.apply(context, args)
    }else {
      timeOutID = setTimeout(() => {
        fn.apply(context, args)
      }, time)
    }
  }
}


/**
 * @desc 函数节流
 * @param func 函数
 * @param wait 延迟执行毫秒数
 * @param type 1 表时间戳版，2 表定时器版
 */
const throttle  = (func, wait ,type) => {
  let previous, timeout;
  if(type===1){
    previous = 0;
  }else if(type===2){
    timeout;
  }
  return function() {
    let context = this;
    let args = arguments;
    if(type===1){
      let now = Date.now();

      if (now - previous > wait) {
        func.apply(context, args);
        previous = now;
      }
    }else if(type===2){
      if (!timeout) {
        timeout = setTimeout(() => {
          timeout = null;
          func.apply(context, args)
        }, wait)
      }
    }
  }
}
//递归操作树
const  renderNode = (treeData, cb) => {
    const loop = data => data.map((item) => {
        if (item.children) {
            return cb(item, loop(item.children)); // item children Item
        }

        return cb(item); // 叶子节点
    });
    return loop(treeData);
}

//深拷贝
const deepCopy = (obj) => {
  //判断是否为数组对象
  let cloneObj = Array.isArray(obj) ? [] : {};
  if(!obj && typeof obj!=="object") return;
  for (let key in obj) {
    if (obj.hasOwnProperty(key)) {
      //判断对象中是否还存在对象通过递归将其copy到新对象
      if (obj[key] && typeof obj[key] === 'object' ) {
        cloneObj[key]  = deepCopy(obj[key]);
      } else {
        cloneObj[key] = obj[key];
      }
    }
  }
  return cloneObj;
}

//数组扁平化
function flatten(arr) {
  return arr.reduce((flat, item) => {
    return flat.concat(Array.isArray(item) ? flatten(item) : item);
  }, [])
}
//数组去重
function unique(arr) {
  return Array.from(new Set(arr));
}

//十进制转化为36进制
function getDex236(number) {
  let unit = ['a', 'b', 'c', 'd','e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'],
    value = '';
  if(number < 10) {
    value = number;
  }else {
    let dis = Math.floor(number / 36);
    let res = number % 36;
    if(dis>= 0 && dis <= 10) {
      value += dis;
    }else if(dis > 10) {
      value += getDex236(dis);
    }
    if(res >= 10){
      value += unit[res-10];
    }else{
      value += res;
    }
  }
  return value;
}

//冒泡排序
function bubbleSort(arr) {
  if(!Array.isArray(arr)) return;
  //控制循环
  for(let i = 0; i < arr.length; i++ ) {
    //控制先后顺序
    for(let j = 0; j < arr.length - i - 1; j ++ ) {
      //临时变量temp用来存储变量
      if(arr[j] > arr [j + 1]) {
        let temp = arr[j];
        arr[j] = arr[j + 1];
        arr[j + 1] = temp;
      }
    }
  }
  return arr;
}

//快速排序
function quickSort(array) {
  if(!Array.isArray(array)) return;
  const sort = (arr, left = 0, right = arr.length - 1) => {
    if (left >= right) {//如果左边的索引大于等于右边的索引说明整理完毕
      return
    }
    let i = left
    let j = right
    const baseVal = arr[j] // 取无序数组最后一个数为基准值
    while (i < j) {//把所有比基准值小的数放在左边大的数放在右边
      while (i < j && arr[i] <= baseVal) { //找到一个比基准值大的数交换
        i++
      }
      arr[j] = arr[i] // 将较大的值放在右边如果没有比基准值大的数就是将自己赋值给自己（i 等于 j）
      while (j > i && arr[j] >= baseVal) { //找到一个比基准值小的数交换
        j--
      }
      arr[i] = arr[j] // 将较小的值放在左边如果没有找到比基准值小的数就是将自己赋值给自己（i 等于 j）
    }
    arr[j] = baseVal // 将基准值放至中央位置完成一次循环（这时候 j 等于 i ）
    sort(arr, left, j-1) // 将左边的无序数组重复上面的操作
    sort(arr, j+1, right) // 将右边的无序数组重复上面的操作
  }
  const newArr = array.concat() // 为了保证这个函数是纯函数拷贝一次数组
  sort(newArr);
  return newArr
}
//获得level
function getlevel($value, $dec = 1)
{
  if ($value >= 1024 * 1024 * 1000) return (round($value/1024/1024/1000,$dec))+ 'G';
  if ($value >= 1024 * 1024) return (round($value/1024/1024,$dec))+ 'M';
  if ($value >= 1024 * 100) return (round($value/1024/1024,$dec)) + 'M';
  if ($value < 1024 * 100 && $value >= 100) return (round($value/1024,$dec)) + 'K';
  /*if ($value < 1024 and $value >= 100) return round($value/1024,$dec).'K';*/
  if ($value < 100 && $value >= 1) return round($value,$dec);
  if ($value < 1) return $value.toFixed(2);
}


export {
  deepCopy,
  formatCount,
  formatDate,
  formatMilliSecond,
  formatRateTo4,
  formatSize,
  debounce,
  throttle,
  copyText,
  dateFormatToStamp,
  renderNode,
  exportData,
  padLeftZero,
  parseDate,
  stopBubble,
  flatten,
  unique,
  getDex236,
  bubbleSort,
  quickSort,
  getlevel
}
