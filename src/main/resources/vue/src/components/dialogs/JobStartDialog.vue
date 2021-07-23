<template>
  <v-dialog v-model="dialog" max-width="600px">
    <template v-slot:activator="{ on, attrs }">
      <v-btn v-bind="attrs" v-on="on" class="ma-2 white--text" color="blue-grey">
        <v-progress-circular v-if="progress" indeterminate color="white"></v-progress-circular>
        <template v-else>
          수동 실행
          <v-icon dark right>mdi-play</v-icon>
        </template>
      </v-btn>
    </template>
    <v-card>
      <v-card-title>
        <span class="text-h5">수동 실행</span>
      </v-card-title>
      <v-card-text>
        <v-container>
          <v-row>
            <v-col cols="12">
              <v-menu ref="calendarMenu" v-model="calendarMenu" :close-on-content-click="false"
                      :return-value.sync="dates" transition="scale-transition" offset-y
                      min-width="auto">
                <template v-slot:activator="{ on, attrs }">
                  <v-text-field v-model="dateRangeText" prepend-icon="mdi-calendar"
                                label="데이터 조회 기간" v-bind="attrs" v-on="on" required
                                readonly></v-text-field>
                </template>
                <v-date-picker v-model="dates" @input="menu2 = false" show-current range no-title
                               scrollable>
                  <v-spacer></v-spacer>
                  <v-btn text color="primary" @click="menu = false">
                    취소
                  </v-btn>
                  <v-btn text color="primary" @click="$refs.calendarMenu.save(dates)">
                    확인
                  </v-btn>
                </v-date-picker>
              </v-menu>
            </v-col>
            <v-col cols="12">
              <v-select v-model="form.api" :items="jobSelect" item-text="text"
                        item-value="api" label="배치 종류" required></v-select>
            </v-col>
          </v-row>
        </v-container>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="blue darken-1" text @click="dialog = false">
          취소
        </v-btn>
        <v-btn color="blue darken-1" text @click="executeJob()">
          시작
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
import {mapMutations} from "vuex";

export default {
  name: "JobStartDialog",
  methods: {
    ...mapMutations(['showDialog']),
    executeJob() {
      this.dialog = false;
      this.progress = true;
      this.$http.post('api/run/' + this.form.api, this.getFormData())
      .then(response => {
        console.log(response);
        this.progress = false;
        this.$emit('getHistoryList');
        this.showDialog({title: '작업 완료', message: '작업을 진행 하였습니다.'});
        setTimeout(() => this.dialog.show = false, 5000);
      })
      .catch(error => {
        console.log(error);
        this.showDialog({title: '에러 상세 정보', message: error.message});
        this.$emit('getHistoryList');
        this.progress = false;
      });
    },
    getFormData() {
      this.form.fromDate = this.dates[0];
      this.form.toDate = this.dates[1];
      console.log(this.form)
      return this.form;
    },
  },
  data() {
    return {
      dialog: false,
      calendarMenu: false,
      progress: false,
      dates: [],
      form: {
        fromDate: '',
        toDate: '',
        api: ''
      },
      jobSelect: [
        {text: '전체', api: 'all',},
        {text: '품목 등록', api: 'item',},
        {text: '수주 등록', api: 'contract',},
        {text: '발주 생성', api: 'purchase',},
        {text: '미등록 거래처 확인', api: 'customer',},
      ],
    }
  },
  computed: {
    dateRangeText() {
      return this.dates.join(' ~ ')
    },
  },
}
</script>

<style scoped>

</style>
