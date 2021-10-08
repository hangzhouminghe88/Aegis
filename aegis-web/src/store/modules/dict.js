const dict = {
    state: {
        // 经常需要读取的数据字典
        sex: [],
        domainType:[]
    },
    mutations: {
        // 设置值的改变方法
        setSex(state, list) {
            state.sex = list;
        },
        // 设置值的改变方法
        setDomainType(state, list) {
            state.domainType = list;
        },
    }
};

export default dict;
