<template>
  <v-container style = "height: 80vh">
    <v-data-table class = "elevation-1" :headers = "headers" :items = "items" item-key = "jobExecutionId" sort-by = "createTime" sort-desc height = "80vh" :expanded.sync = "expanded" single-expand>
      <template v-slot:item = "{ item, expand, isExpanded }">
        <tr>
          <td class = "text-center">{{ item.jobExecutionId }}</td>
          <td class = "text-center">{{ item.jobInstanceId.jobName }}</td>
          <td class = "text-center">{{ item.createTime }}</td>
          <td class = "text-center">{{ item.startTime }}</td>
          <td class = "text-center">{{ item.endTime }}</td>
          <td class = "text-center">{{ statusTranslate(item.status) }}</td>
          <td class = "text-center">
            <v-chip v-if = "isPurchaseConvertComplete(item.batchStepExecutions)" class = "ma-1" color = "success" outlined label @click.stop = "downloadPurchaseOrder(item.jobExecutionId)">
              다운로드
            </v-chip>
          </td>
          <td class="text-center">
            <v-icon v-if="!isExpanded" @click = "expand(!isExpanded)">mdi-chevron-down</v-icon>
            <v-icon v-else @click = "expand(!isExpanded)">mdi-chevron-up</v-icon>
          </td>
        </tr>
      </template>
      <template v-slot:top>
        <v-toolbar flat>
          <v-toolbar-title>업무 자동화</v-toolbar-title>
          <v-spacer></v-spacer>
          <SettingDialog/>
          <JobPlayDialog/>
        </v-toolbar>
      </template>
      <template v-slot:expanded-item = "{ headers, item }">
        <td :colspan = "headers.length" class = "pa-0">
          <v-stepper alt-labels>
            <v-stepper-header>
              <template v-for = "(step, index) in getJobSteps(item.jobInstanceId.jobName)">
                <v-stepper-step :step = "index + 1" style = "font-size:14px; cursor: pointer;" :key = "index"
                                :complete = "isStepCompleted(item.batchStepExecutions[index])"
                                :rules = "[() => isStepError(item.batchStepExecutions[index])]"
                                @click = "!isStepError(item.batchStepExecutions[index]) ? showCustomersError(item.batchStepExecutions[index]) : false">
                  {{ step }}
                </v-stepper-step>
                <v-divider v-if = "index + 1 < getJobSteps(item.jobInstanceId.jobName).length" :key = "'divider-' + index"></v-divider>
              </template>
            </v-stepper-header>
          </v-stepper>
        </td>
      </template>
    </v-data-table>
    <MessageDialog/>
  </v-container>
</template>

<script>
import {mapMutations} from 'vuex';
import {saveFile} from '@/assets/js/saver'
import MessageDialog from '@/components/dialogs/MessageDialog'
import SettingDialog from '@/components/dialogs/SettingDialog'
import JobPlayDialog from '@/components/dialogs/JobPlayDialog'
export default {
  name: "History",
  components: {
    MessageDialog,
    SettingDialog,
    JobPlayDialog
  },
  mounted() {
    this.getHistoryList();
  },
  methods: {
    ...mapMutations(['showDialog', 'showCustomersError']),
    isStepCompleted(step) {
      return step === undefined ? false : step.exitMessage === '';
    },
    isStepError(step) {
      return step === undefined ? true : step.exitMessage === '';
    },
    isPurchaseConvertComplete(steps) {
      let result = false;
      steps.forEach(step => {
        if (step.stepName === '발주 변환') {
          result = step.exitCode === 'COMPLETED'
        }
      });
      return result;
    },
    getHistoryList() {
      this.$http.get('api/batchJobExecutions?page=0&size=1000&sort=createTime,desc')
          .then(result => this.items = result.data._embedded.batchJobExecutions)
          .catch(error => this.showDialog('에러 상세 정보', error));
    },
    downloadPurchaseOrder(id) {
      this.$http.get('api/purchase-order/' + id, {responseType: 'blob'})
          .then(response => saveFile(response, '구매 발주.zip'))
          .catch(response => console.error('Could not Download the Excel report from the backend.', response));
    },
    statusTranslate(status) {
      switch (status) {
        case 'COMPLETED':
          return '완료'
        case 'FAILED':
          return '실패'
        default :
          return status
      }
    },
    getJobSteps(jobName) {
      return this.jobSteps.find(x => x.jobName === jobName).steps
    }
  },
  data() {
    return {
      jobSteps: [
        {
          jobName: '전체',
          steps: ['초기화', '품목 다운로드', '거래처 다운로드',  '데이터 수집', '거래처 체크', '품목 변환', '수주 변환', '발주 변환', '품목 업로드', '수주 업로드'],
        },
        {
          jobName: '품목 등록',
          steps: ['초기화', '품목 다운로드', '데이터 수집', '품목 변환', '품목 업로드'],
        },
        {
          jobName: '거래처 확인',
          steps: ['초기화', '거래처 다운로드', '데이터 수집', '거래처 체크'],
        },
        {
          jobName: '수주 등록',
          steps: ['초기화', '품목 다운로드', '거래처 다운로드',  '데이터 수집', '거래처 체크', '품목 변환', '수주 변환', '품목 업로드', '수주 업로드'],
        },
        {
          jobName: '발주 변환',
          steps: ['초기화', '품목 다운로드', '데이터 수집', '발주 변환'],
        },
      ],
      items: [],
      expanded: [],
      headers: [
        {text: '배치 번호', align: 'center'},
        {text: '배치 종류', align: 'center'},
        {text: '생성 시간', align: 'center'},
        {text: '시작 시간', align: 'center'},
        {text: '종료 시간', align: 'center'},
        {text: '실행 결과', align: 'center'},
        {text: '발주 데이터', align: 'center', sortable: false},
        {text: '스텝', align: 'center', value: 'data-table-expand', sortable: false},
      ],
    }
  }
}
</script>

<style scoped>
</style>
