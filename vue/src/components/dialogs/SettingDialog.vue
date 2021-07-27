<template>
  <v-dialog v-model="dialog" max-width="800px">
    <template v-slot:activator="{ on, attrs }">
      <v-btn v-bind="attrs" v-on="on" class="ma-2 white--text" color="blue-grey">
        배치 설정
        <v-icon dark right>mdi-cog</v-icon>
      </v-btn>
    </template>
    <v-card>
      <v-card-title>
        <span class="text-h5">배치 환경 설정</span>
      </v-card-title>
      <v-card-text>
        <v-container>
          <v-row>
            <v-col cols="12">
              <v-expansion-panels flat>
                <v-expansion-panel>
                  <v-expansion-panel-header class="pa-0">
                    <v-text-field v-model="form.cron"
                                  :hint="cronToString(form.cron)" label="배치 조건"
                                  persistent-hint
                                  readonly required/>
                  </v-expansion-panel-header>
                  <v-expansion-panel-content class="pa-0 mt-2">
                    <v-card class="pa-2" outlined tile>
                      <VueCronEditorBuefy v-model="form.cron" :custom-locales="i18n"
                                          cronSyntax="quartz" locale="ko"
                                          preserveStateOnSwitchToAdvanced/>
                    </v-card>
                  </v-expansion-panel-content>
                </v-expansion-panel>
              </v-expansion-panels>
            </v-col>
            <v-col cols="12">
              <v-select v-model="form.period" :items="periodSelect" item-text="text"
                        item-value="period" label="조회 기간" required></v-select>
            </v-col>
          </v-row>
        </v-container>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="blue darken-1" text @click="getEnv">
          취소
        </v-btn>
        <v-btn color="blue darken-1" text @click="saveEnv">
          저장
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
import VueCronEditorBuefy from 'vue-cron-editor-buefy';
import cronstrue from 'cronstrue/i18n';
import {mapMutations} from "vuex";

export default {
  name: "SettingDialog",
  components: {
    VueCronEditorBuefy
  },
  created() {
    this.getEnv();
  },
  methods: {
    ...mapMutations(['showDialog']),
    cronToString(cron) {
      return cronstrue
      .toString(cron, {use24HourTimeFormat: true, locale: "ko"})
      .replace("에서", " at ");
    },
    getEnv() {
      this.dialog = false;
      this.$http.get('api/settings/batch/auto')
      .then(response => this.form = response.data)
      .catch(error => this.showDialog({title: '에러 상세 정보', message: error.message}));
    },
    saveEnv() {
      this.dialog = false;
      this.$http.put('api/settings/batch', this.form)
      .then(response => this.form = response.data)
      .catch(error => this.showDialog({title: '에러 상세 정보', message: error.message}));
    }
  },
  data() {
    return {
      dialog: false,
      form: {
        type: "auto",
        cron: "0 10 9 ? * *",
        period: 6,
      },
      periodSelect: [
        {
          text: '하루 전',
          period: 1,
        },
        {
          text: '일주일 전',
          period: 6,
        },
        {
          text: '한달 전',
          period: 30,
        }
      ],
      i18n: {
        ko: {
          every: "매",
          mminutes: "분 마다",
          hoursOnMinute: "시간 마다",
          daysAt: "일 마다",
          at: "에",
          onThe: "매",
          dayOfEvery: "일에",
          monthsAt: "개월 마다",
          everyDay: "매주",
          mon: "월",
          tue: "화",
          wed: "수",
          thu: "목",
          fri: "금",
          sat: "토",
          sun: "일",
          hasToBeBetween: "Has to be between",
          and: "and",
          minutes: "매분",
          hourly: "매시간",
          daily: "매일",
          weekly: "매주",
          monthly: "매월",
          advanced: "고급",
          cron: "cron 표현식 :"
        }
      },
    }
  }
}
</script>

<style scoped>

</style>
