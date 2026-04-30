import 'vuetify/styles'
import '@mdi/font/css/materialdesignicons.css'

import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import { aliases, mdi } from 'vuetify/iconsets/mdi'

const vuetify = createVuetify({
  components,
  directives,
  theme: {
    defaultTheme: 'projectPulse',
    themes: {
      projectPulse: {
        dark: false,
        colors: {
          primary: '#2563eb',
          secondary: '#eff6ff',
          accent: '#1d4ed8',
          success: '#16a34a',
          warning: '#ea580c',
          error: '#dc2626',
          surface: '#ffffff',
          background: '#f7f7f7',
        },
      },
    },
  },
  defaults: {
    VContainer: {
      maxWidth: '1200',
    },
    VCard: {
      rounded: 'xl',
      elevation: 0,
    },
    VBtn: {
      rounded: 'lg',
      elevation: 0,
      size: 'large',
      variant: 'elevated',
    },
    VTextField: {
      variant: 'outlined',
      density: 'comfortable',
      hideDetails: 'auto',
      rounded: 'lg',
    },
    VSelect: {
      variant: 'outlined',
      density: 'comfortable',
      hideDetails: 'auto',
      rounded: 'lg',
    },
    VAutocomplete: {
      variant: 'outlined',
      density: 'comfortable',
      hideDetails: 'auto',
      rounded: 'lg',
    },
    VTextarea: {
      variant: 'outlined',
      density: 'comfortable',
      hideDetails: 'auto',
      rounded: 'lg',
    },
    VAlert: {
      rounded: 'xl',
      variant: 'tonal',
    },
    VChip: {
      rounded: 'pill',
    },
    VTab: {
      rounded: 'pill',
    },
  },
  icons: {
    defaultSet: 'mdi',
    aliases,
    sets: {
      mdi,
    },
  },
})

export default vuetify
