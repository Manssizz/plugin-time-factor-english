<template>
  <div class="image-preview">
    <div class="preview-header">
      <h4>Dynamic OG Image Preview</h4>
      <p>Preview how your Open Graph images will appear on social media</p>
    </div>

    <div class="preview-controls">
      <div class="control-group">
        <label>Title:</label>
        <input
          v-model="previewData.title"
          @input="updatePreview"
          class="preview-input"
          placeholder="Enter post title"
        />
      </div>

      <div class="control-group">
        <label>Description:</label>
        <textarea
          v-model="previewData.description"
          @input="updatePreview"
          class="preview-textarea"
          placeholder="Enter post description"
          rows="3"
        ></textarea>
      </div>

      <div class="control-group">
        <label>Background Image:</label>
        <input
          type="file"
          @change="handleImageUpload"
          accept="image/*"
          class="file-input"
        />
      </div>
    </div>

    <div class="preview-container">
      <div class="og-image-preview" :style="previewStyle">
        <div class="og-content">
          <div class="og-title">{{ previewData.title || 'Your Post Title' }}</div>
          <div class="og-description">{{ previewData.description || 'Your post description will appear here...' }}</div>
          <div class="og-site-info">
            <span class="og-site-name">{{ siteName }}</span>
          </div>
        </div>
        <div class="og-overlay"></div>
      </div>

      <div class="preview-info">
        <div class="info-item">
          <span class="info-label">Dimensions:</span>
          <span class="info-value">{{ ogImageWidth }} × {{ ogImageHeight }} px</span>
        </div>
        <div class="info-item">
          <span class="info-label">Recommended:</span>
          <span class="info-value">1200 × 630 px</span>
        </div>
        <div class="info-item">
          <span class="info-label">Format:</span>
          <span class="info-value">PNG/JPG</span>
        </div>
      </div>
    </div>

    <div class="preview-actions">
      <button @click="generateImage" class="generate-button" :disabled="!canGenerate">
        Generate OG Image
      </button>
      <button @click="downloadImage" class="download-button" :disabled="!generatedImageUrl">
        Download Image
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive } from 'vue'

interface PreviewData {
  title: string
  description: string
  backgroundImage?: string
}

const props = defineProps<{
  ogImageWidth: number
  ogImageHeight: number
  siteName: string
}>()

const previewData = reactive<PreviewData>({
  title: 'Sample Post Title',
  description: 'This is a sample description for your Open Graph image preview. It shows how your content will appear when shared on social media platforms.',
  backgroundImage: undefined
})

const generatedImageUrl = ref<string>('')

const previewStyle = computed(() => ({
  width: `${props.ogImageWidth}px`,
  height: `${props.ogImageHeight}px`,
  backgroundImage: previewData.backgroundImage ? `url(${previewData.backgroundImage})` : 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
  backgroundSize: 'cover',
  backgroundPosition: 'center'
}))

const canGenerate = computed(() => {
  return previewData.title.trim().length > 0 && previewData.description.trim().length > 0
})

const updatePreview = () => {
  // Preview updates automatically through reactive data
}

const handleImageUpload = (event: Event) => {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]

  if (file) {
    const reader = new FileReader()
    reader.onload = (e) => {
      previewData.backgroundImage = e.target?.result as string
    }
    reader.readAsDataURL(file)
  }
}

const generateImage = () => {
  // TODO: Implement actual image generation using Canvas API
  console.log('Generating OG image with data:', previewData)

  // For now, just set a placeholder
  generatedImageUrl.value = 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg=='
}

const downloadImage = () => {
  if (!generatedImageUrl.value) return

  const link = document.createElement('a')
  link.href = generatedImageUrl.value
  link.download = 'og-image.png'
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}
</script>

<style scoped>
.image-preview {
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.preview-header {
  margin-bottom: 20px;
}

.preview-header h4 {
  margin: 0 0 8px 0;
  color: #1f2937;
  font-size: 16px;
  font-weight: 600;
}

.preview-header p {
  margin: 0;
  color: #6b7280;
  font-size: 14px;
}

.preview-controls {
  display: grid;
  gap: 16px;
  margin-bottom: 24px;
}

.control-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.control-group label {
  font-weight: 500;
  color: #374151;
  font-size: 14px;
}

.preview-input,
.preview-textarea {
  padding: 8px 12px;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  font-size: 14px;
  width: 100%;
}

.preview-input:focus,
.preview-textarea:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1);
}

.file-input {
  padding: 8px;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  font-size: 14px;
}

.preview-container {
  display: flex;
  gap: 24px;
  margin-bottom: 24px;
}

.og-image-preview {
  position: relative;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  transform: scale(0.5);
  transform-origin: top left;
  width: calc(1200px * 0.5);
  height: calc(630px * 0.5);
}

.og-content {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 40px;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.8));
  color: white;
}

.og-title {
  font-size: 48px;
  font-weight: 700;
  margin-bottom: 16px;
  line-height: 1.2;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
}

.og-description {
  font-size: 24px;
  line-height: 1.4;
  margin-bottom: 20px;
  opacity: 0.9;
}

.og-site-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.og-site-name {
  font-size: 18px;
  font-weight: 600;
  opacity: 0.8;
}

.preview-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
  min-width: 200px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #e5e7eb;
}

.info-item:last-child {
  border-bottom: none;
}

.info-label {
  font-weight: 500;
  color: #6b7280;
  font-size: 14px;
}

.info-value {
  font-weight: 600;
  color: #1f2937;
  font-size: 14px;
}

.preview-actions {
  display: flex;
  gap: 12px;
}

.generate-button,
.download-button {
  padding: 10px 16px;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.generate-button {
  background: #3b82f6;
  color: white;
}

.generate-button:hover:not(:disabled) {
  background: #2563eb;
}

.generate-button:disabled {
  background: #9ca3af;
  cursor: not-allowed;
}

.download-button {
  background: #10b981;
  color: white;
}

.download-button:hover:not(:disabled) {
  background: #059669;
}

.download-button:disabled {
  background: #9ca3af;
  cursor: not-allowed;
}
</style>
