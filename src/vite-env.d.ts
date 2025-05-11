/// <reference types="vite/client" />

interface ViteTypeOptions {
  // By adding this line, you can make the type of ImportMetaEnv strict
  // to disallow unknown keys.
  // strictImportMetaEnv: unknown
}

interface ImportMetaEnv {
  readonly VITE_TEST_SITE: string
  readonly VITE_TEST_IMAGE_SITE: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}
