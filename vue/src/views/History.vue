<template>
  <v-container style="height: 80vh">
    <v-data-table :expanded.sync="expanded" :headers="headers" :items="items" class="elevation-1" height="80vh"
                  item-key="jobExecutionId" single-expand sort-by="createTime" sort-desc>
      <template v-slot:item="{ item, expand, isExpanded }">
        <tr>
          <td>{{ item.jobExecutionId }}</td>
          <td>{{ item.jobInstanceId.jobName }}</td>
          <td>{{ item.startTime }}</td>
          <td>{{ item.endTime }}</td>
          <td>{{ item.batchJobExecutionParams.find(x => x.keyName === 'fromDateStr').stringVal }}</td>
          <td>{{ item.batchJobExecutionParams.find(x => x.keyName === 'toDateStr').stringVal }}</td>
          <td>{{ statusTranslate(item.status) }}</td>
          <td class="text-center">
            <v-chip v-if="isPurchaseConvertComplete(item.batchStepExecutions)" class="ma-1"
                    color="success" label outlined
                    @click.stop="downloadPurchaseOrder(item.jobExecutionId)">
              다운로드
            </v-chip>
          </td>
          <td class="text-center">
            <v-icon v-if="!isExpanded" @click="expand(!isExpanded)">mdi-chevron-down</v-icon>
            <v-icon v-else @click="expand(!isExpanded)">mdi-chevron-up</v-icon>
          </td>
        </tr>
      </template>
      <template v-slot:top>
        <v-toolbar flat>
          <v-toolbar-title>업무 자동화</v-toolbar-title>
          <v-spacer></v-spacer>
          <SettingDialog/>
          <JobStartDialog @getHistoryList="getHistoryList"/>
        </v-toolbar>
      </template>
      <template v-slot:expanded-item="{ headers, item }">
        <td :colspan="headers.length" class="pa-0">
          <v-stepper alt-labels>
            <v-stepper-header>
              <template v-for="(step, index) in getJobSteps(item.jobInstanceId.jobName)">
                <v-stepper-step :key="index"
                                :complete="isStepCompleted(item.batchStepExecutions[index])"
                                :rules="[() => isStepError(item.batchStepExecutions[index])]"
                                :step="index + 1"
                                style="font-size:14px; cursor: pointer;"
                                @click="!isStepError(item.batchStepExecutions[index]) ?
                                showCustomersError(item.batchStepExecutions[index]) : false">
                  {{ step }}
                </v-stepper-step>
                <v-divider v-if="index + 1 < getJobSteps(item.jobInstanceId.jobName).length"
                           :key="'divider-' + index"></v-divider>
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
import {mapGetters, mapMutations} from 'vuex';
import {saveFile} from '@/assets/js/saver'
import MessageDialog from '@/components/dialogs/MessageDialog'
import SettingDialog from '@/components/dialogs/SettingDialog'
import JobStartDialog from '@/components/dialogs/JobStartDialog'

export default {
  name: "History",
  components: {
    MessageDialog,
    SettingDialog,
    JobStartDialog
  },
  mounted() {
    this.getHistoryList();
  },
  computed: {
    ...mapGetters(['getJobSteps'])
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
      .catch(error => this.showDialog({title: '에러 상세 정보', message: error.message}));
    },
    downloadPurchaseOrder(id) {
      this.$http.get('api/purchase-order/' + id, {responseType: 'blob'})
      .then(response => saveFile(response, '구매 발주.zip'))
      .catch(error => this.showDialog({title: '에러 상세 정보', message: error.message}));
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
  },
  data() {
    return {
      items: [],
      expanded: [],
      headers: [
        {text: '배치 번호'},
        {text: '배치 종류'},
        {text: '시작 시간'},
        {text: '종료 시간'},
        {text: '조회 시작 일'},
        {text: '조회 종료 일 '},
        {text: '실행 결과'},
        {text: '발주 데이터', align: 'center', sortable: false},
        {text: '스텝', align: 'center', value: 'data-table-expand', sortable: false},
      ],
    }
  }
}
</script>

<style scoped>
</style>
