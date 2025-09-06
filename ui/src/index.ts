import { App } from 'vue'
import ImageOptimizationPanel from './components/ImageOptimizationPanel.vue'
import ImagePreview from './components/ImagePreview.vue'

export default {
  install(app: App) {
    app.component('ImageOptimizationPanel', ImageOptimizationPanel)
    app.component('ImagePreview', ImagePreview)
  }
}
