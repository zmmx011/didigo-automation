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
    jobSteps: [
      {
        jobName: '거래처 등록',
        steps: [
          '초기화',
          '거래처 다운로드',
          '데이터 수집',
          '거래처 변환',
          '거래처 업로드'
        ],
      },
      {
        jobName: '품목 단가 등록',
        steps: [
          '초기화',
          '품목 다운로드',
          '단가 다운로드',
          '거래처 다운로드',
          '데이터 수집',
          '품목 단가 변환',
          '거래처 변환',
          '품목 업로드',
          '거래처 업로드',
          '단가 업로드',
        ],
      },
      {
        jobName: '수주 등록',
        steps: [
          '초기화',
          '품목 다운로드',
          '단가 다운로드',
          '거래처 다운로드',
          '수주 다운로드',
          '데이터 수집',
          '거래처 변환',
          '품목 단가 변환',
          '수주 변환',
          '품목 업로드',
          '거래처 업로드',
          '단가 업로드',
          '수주 업로드'
        ],
      },
    ],
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
    getJobSteps: (state) => (jobName) => {
      return state.jobSteps.find(x => x.jobName === jobName).steps
    }
  },
  mutations: {
    showDialog(state, payload) {
      state.messageDialog.title = payload.title;
      state.messageDialog.message = payload.message;
      state.messageDialog.show = true;
    },
    showCustomersError(state, payload) {
      let url = payload._links.jobExecutionId.href;
      state.messageDialog.jobExecutionId = url.substring(
          url.lastIndexOf("/") + 1);
      state.messageDialog.title = '에러 상세 정보';
      state.messageDialog.message = payload.exitMessage.replace(':', '\n');
      state.messageDialog.show = true;
    },
  },
  actions: {},
  modules: {}
})
