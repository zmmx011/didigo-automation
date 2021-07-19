<template>
  <v-dialog v-model = "dialog" max-width = "1200px">
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
              <VueCronEditorBuefy v-model="cronExpression" cronSyntax="quartz" :visibleTabs="visibleTab" locale="ko"
                                  :custom-locales="i18n"/>
              {{cronExpression}}
            </v-col>
            <v-col cols = "12">
              <v-text-field label = "Email*" required></v-text-field>
            </v-col>
            <v-col cols = "12">
              <v-text-field label = "Password*" type = "password" required></v-text-field>
            </v-col>
            <v-col cols = "12" sm = "6">
              <v-select :items = "['0-17', '18-29', '30-54', '54+']" label = "Age*" required></v-select>
            </v-col>
            <v-col cols = "12" sm = "6">
              <v-autocomplete :items = "['Skiing', 'Ice hockey', 'Soccer', 'Basketball', 'Hockey', 'Reading', 'Writing', 'Coding', 'Basejump']" label = "Interests" multiple></v-autocomplete>
            </v-col>
          </v-row>
        </v-container>
        <small>*indicates required field</small>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color = "blue darken-1" text @click = "dialog = false">
          Close
        </v-btn>
        <v-btn color = "blue darken-1" text @click = "dialog = false">
          Save
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
import VueCronEditorBuefy from 'vue-cron-editor-buefy';
export default {
  name: "SettingDialog",
  components: {
    VueCronEditorBuefy
  },
  data() {
    return {
      dialog: false,
      cronExpression: "*/1 * * * *",
      visibleTab: ['minutes', 'hourly', 'daily', 'weekly', 'monthly'],
      i18n: {
        ko: {
          every: "매",
          mminutes: "분 마다",
          hoursOnMinute: "시간 마다",
          daysAt: "일 마다",
          at: "에",
          onThe: "매월",
          dayOfEvery: "day, of every",
          monthsAt: "month(s), at",
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
          minutes: "MINUTES",
          hourly: "HOURLY",
          daily: "DAILY",
          weekly: "WEEKLY",
          monthly: "MONTHLY",
          advanced: "ADVANCED",
          cronExpression: "cron expression:"
        }
      },
    }
  }
}
</script>

<style scoped>

</style>
