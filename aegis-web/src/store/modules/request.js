import * as types from '../mutation-types'
let state = {
  cancelTokenArr: []
}

const actions = {
  addCancelRequest: ({commit}, payload) => {
    commit(types.ADDCANCELREQUEST, payload)
  },
  clearCancelRequest: ({commit}, payload) => {
    commit(types.CLEARCANCELREQUEST, payload);
  }
}

const mutations = {
  [types.ADDCANCELREQUEST]: (state, payload) => {
    state.cancelTokenArr.push(payload.cancelToken)
  },
  [types.CLEARCANCELREQUEST]: (state, payload) => {
    if(state.cancelTokenArr.length > 0)
    state.cancelTokenArr.forEach(item => {
      item({'status': 'CANCEL', error: '取消请求'})
    })
    state.cancelTokenArr = []
  }
}

export  default {
  state,
 actions,
  mutations
}
