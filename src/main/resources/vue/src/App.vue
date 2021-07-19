<template>
  <v-app>
    <v-main>
      <History/>
    </v-main>
  </v-app>
</template>

<script>
import History from '@/views/History';

export default {
  name: 'App',

  components: {
    History,
  },

  data: () => ({
    //
  }),
};
</script>
