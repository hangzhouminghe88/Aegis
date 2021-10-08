const hasPermission = {
    install(Vue, options) {
        Vue.directive('has', {
            inserted(el, binding, vnode) {
                let permTypes = vnode.context.$route.meta.permTypes;
                if (permTypes && !permTypes.includes(binding.value)) {
                    el.parentNode.removeChild(el);
                }
            }
        });
        //获得用户权限
        let getUserRole = (values) => {
            let roles = JSON.parse(localStorage.getItem('userInfo')).roles;
            return roles.some(item => values.includes(item.name));
        }
        Vue.directive('permission', {
            bind: (el, binding, vnode) => {
                //若有操作权限则按钮灰化不能操作
                if (!getUserRole(binding.value)) {
                    el.setAttribute('disabled', true);
                    el.classList.add('no-permission');
                } else {
                    //若有权限则去掉灰化
                    el.removeAttribute('disabled');
                    el.classList.remove('no-permission');
                }
            },
            update: (el, binding, vnode) => {
                //若有操作权限则按钮灰化不能操作
                if (!getUserRole(binding.value)) {
                    el.classList.add('no-permission');
                    el.setAttribute('disabled', true);
                } else {
                    el.removeAttribute('disabled');
                    el.classList.remove('no-permission');
                }
            },
            unbind: (el, binding, vnode) => {
                el.removeAttribute('disabled');
                if (el.classList && el.classList.contains('no-permission')) {
                    el.classList.remove('no-permission');
                }
            }
        })
    }
};

export default hasPermission;
