<template>
  <v-dialog v-model = "dialog" max-width = "800px">
    <template v-slot:activator = "{ on, attrs }">
      <v-btn color = "blue-grey" class = "ma-2 white--text" v-bind = "attrs" v-on = "on">
        배치 설정
        <v-icon right dark>mdi-cog</v-icon>
      </v-btn>
    </template>
    <v-card>
      <v-card-title>
        <span class = "text-h5">배치 환경 설정</span>
      </v-card-title>
      <v-card-text>
        <v-container>
          <v-row>
            <v-col cols = "12">
              <v-expansion-panels flat>
                <v-expansion-panel>
                  <v-expansion-panel-header class="pa-0">
                    <v-text-field label = "배치 조건" required readonly v-model = "form.cronExpression"
                                  :hint="cronToString(form.cronExpression)" persistent-hint/>
                  </v-expansion-panel-header>
                  <v-expansion-panel-content class="pa-0 mt-2">
                    <v-card tile outlined class="pa-2">
                      <VueCronEditorBuefy v-model="form.cronExpression" cronSyntax="quartz" locale="ko"
                                          :custom-locales="i18n" preserveStateOnSwitchToAdvanced/>
                    </v-card>
                  </v-expansion-panel-content>
                </v-expansion-panel>
              </v-expansion-panels>
            </v-col>
            <v-col cols = "12" sm = "6">
              <v-select :items = "['1일', '1주일', '1개월']" label = "조회 기간" required></v-select>
            </v-col>
            <v-col cols = "12" sm = "6">
              <v-autocomplete :items = "['디디고 몰', 'KD', 'Cozy']" label = "대상 사이트" multiple></v-autocomplete>
            </v-col>
          </v-row>
        </v-container>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color = "blue darken-1" text @click = "dialog = false">
          취소
        </v-btn>
        <v-btn color = "blue darken-1" text @click = "cronSave(form)">
          저장
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
import VueCronEditorBuefy from 'vue-cron-editor-buefy';
import cronstrue from 'cronstrue/i18n';
export default {
  name: "SettingDialog",
  components: {
    VueCronEditorBuefy
  },
  methods: {
    cronToString(cron) {
      return cronstrue
          .toString(cron, { use24HourTimeFormat: true, locale: "ko" })
          .replace("에서", " at ");
    },
    cronSave(cron) {
      console.log(cron);
    }
  },
  data() {
    return {
      dialog: false,
      form: {
        cronExpression: "0 10 9 ? * MON",
      },
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
          cronExpression: "cron 표현식 :"
        }
      },
    }
  }
}
</script>

<style scoped>

</style>
