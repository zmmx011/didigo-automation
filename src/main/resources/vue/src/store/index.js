import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    messageDialog: {
      show: false,
      title: '',
      message: '',
      jobExecutionId: ''
    },
  },
  getters: {
    getMessageDialog(state) {
      return state.messageDialog;
    },
    isCustomersDownload(state) {
      let result = false;
      if (state.messageDialog.message !== null) {
        result = state.messageDialog.message.includes('미등록')
      }
      return result
    },
  },
  mutations: {
    showDialog(state, payload) {
      state.messageDialog.title = payload.title;
      state.messageDialog.message = payload.message;
      state.messageDialog.show = true;
    },
    showCustomersError(state, payload) {
      let url = payload._links.jobExecutionId.href;
      state.messageDialog.jobExecutionId = url.substring(url.lastIndexOf("/") + 1);
      state.messageDialog.title = '에러 상세 정보';
      state.messageDialog.message =payload.exitMessage.replace(':', '\n');
      state.messageDialog.show = true;
    },
  },
  actions: {
  },
  modules: {
  }
})
