/// <reference types="vite/client" />

// eslint-disable-next-line @typescript-eslint/no-empty-object-type
interface ViteTypeOptions {}

interface ImportMetaEnv {
  readonly VITE_TEST_SITE: string
  readonly VITE_TEST_IMAGE_SITE: string
}

interface ImportMeta {readonly env: ImportMetaEnv}
