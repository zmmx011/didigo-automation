<template>
  <v-dialog v-model="getMessageDialog.show" width="500">
    <v-card>
      <v-card-title>{{ getMessageDialog.title }}</v-card-title>
      <v-card-text style="white-space: pre-line">
        {{ getMessageDialog.message }}
      </v-card-text>
      <v-card-actions v-if="isCustomersDownload">
        <v-spacer></v-spacer>
        <v-btn color="primary" text @click="downloadUnregisteredCustomers">
          미등록 거래처 목록 다운로드
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
import {mapGetters} from 'vuex'
import {saveFile} from '@/assets/js/saver'

export default {
  name: "MessageDialog",
  computed: {
    ...mapGetters(['getMessageDialog', 'isCustomersDownload']),
  },
  methods: {
    downloadUnregisteredCustomers() {
      this.$http.get('api/unregistered-customer/' + this.getMessageDialog.jobExecutionId,
          {responseType: 'blob'})
      .then(response => saveFile(response, '미등록 거래처.xlsx'))
      .catch(error => this.showDialog({title: '에러 상세 정보', message: error.message}));
    },
  }
}
</script>

<style scoped>

</style>
