import type { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'com.konformist.comicsreader',
  appName: 'Comics Reader',
  webDir: 'dist',
  zoomEnabled: true,
  backgroundColor: '#121212',
  android: { adjustMarginsForEdgeToEdge: 'auto' },
  plugins: { Keyboard: { resizeOnFullScreen: true } },
};

export default config;
