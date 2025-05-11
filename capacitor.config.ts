import type { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'com.konformist.comicsreader',
  appName: 'Comics Reader',
  webDir: 'dist',
  zoomEnabled: true,
  android: {
    adjustMarginsForEdgeToEdge: 'auto',
  },
  plugins: {
    Keyboard: {
      resizeOnFullScreen: true,
    },
    CapacitorHttp: {
      enabled: true,
    },
  },
};

export default config;
