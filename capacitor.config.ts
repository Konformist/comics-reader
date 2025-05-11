import type { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'com.konformist.comicsreader',
  appName: 'comics-reader',
  webDir: 'dist',
  zoomEnabled: true,
  android: {
    adjustMarginsForEdgeToEdge: 'auto',
  },
  plugins: {
    CapacitorHttp: {
      enabled: true,
    },
  },
};

export default config;
