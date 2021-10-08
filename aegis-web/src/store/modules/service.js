import * as types from '../mutation-types';
import { getRequest } from '@/libs/axios';
import Vue from 'vue';
import { Message } from 'view-design';
let state = {
	serviceList: [],
	selectService: ''
}

let actions = {
   [types.QUERYALLSERVICE]: ({commit}, payload) => {
			commit(types.QUERYALLSERVICE, payload);
	 },
	 [types.SETSELECTSERVICE]: ({commit}, payload) => {
		 commit(types.SETSELECTSERVICE, payload);
	 }
}

let mutations = {
  [types.QUERYALLSERVICE]: (state, payload) => {
		if(payload) {
			Vue.set(state, 'serviceList', [...payload]);
		} else {
			Vue.set(state, 'serviceList', []);
		} 
	},
	[types.SETSELECTSERVICE]: (state, payload) => {
		Vue.set(state, 'selectService', payload);
	}
}

let getters = {
   getCurrentService: (state) => {
     if(state.selectService) {
			 return state.selectService;
		 }else {
       if(state.serviceList && state.serviceList.length <=0) {
         Message.info('请添加实例!');
       }
			 return state.serviceList && state.serviceList[0] && state.serviceList[0].name;
		 }
	 },
	 getServiceList: (state) =>  {
		 console.log(state);
		 return state.serviceList;
	 }
}

export default {
	state,
	actions,
	mutations,
	getters
}
