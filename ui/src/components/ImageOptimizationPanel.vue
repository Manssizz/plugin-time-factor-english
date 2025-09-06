<template>
  <div class="image-optimization-panel">
    <div class="panel-header">
      <h3>Image Optimization Settings</h3>
      <p>Configure automatic image optimization features for better SEO performance</p>
    </div>

    <div class="settings-grid">
      <div class="setting-item">
        <label class="setting-label">
          <input
            type="checkbox"
            v-model="settings.enableImageOptimization"
            @change="updateSettings"
          />
          Enable Image Optimization
        </label>
        <p class="setting-description">
          Enable comprehensive image optimization features
        </p>
      </div>

      <div class="setting-item" v-if="settings.enableImageOptimization">
        <label class="setting-label">
          <input
            type="checkbox"
            v-model="settings.enableAutoAltText"
            @change="updateSettings"
          />
          Automatic Alt Text Generation
        </label>
        <p class="setting-description">
          Generate SEO-friendly alt text for images using AI
        </p>
      </div>

      <div class="setting-item" v-if="settings.enableImageOptimization">
        <label class="setting-label">
          <input
            type="checkbox"
            v-model="settings.enableImageFileOptimization"
            @change="updateSettings"
          />
          Image File Name Optimization
        </label>
        <p class="setting-description">
          Optimize image file names for better SEO
        </p>
      </div>

      <div class="setting-item" v-if="settings.enableImageOptimization">
        <label class="setting-label">
          <input
            type="checkbox"
            v-model="settings.enableDynamicOGImages"
            @change="updateSettings"
          />
          Dynamic Open Graph Images
        </label>
        <p class="setting-description">
          Generate dynamic Open Graph images for social media
        </p>
      </div>

      <div class="setting-item" v-if="settings.enableImageOptimization">
        <label class="setting-label">
          <input
            type="checkbox"
            v-model="settings.enableLazyLoading"
            @change="updateSettings"
          />
          Lazy Loading
        </label>
        <p class="setting-description">
          Implement lazy loading for improved page performance
        </p>
      </div>

      <div class="setting-item" v-if="settings.enableImageOptimization && settings.enableAutoAltText">
        <label class="setting-label">AI Service Provider</label>
        <select
          v-model="settings.aiServiceProvider"
          @change="updateSettings"
          class="setting-select"
        >
          <option value="openai">OpenAI</option>
          <option value="google_vision">Google Vision AI</option>
          <option value="azure_cv">Azure Computer Vision</option>
        </select>
        <p class="setting-description">
          Choose AI service for image analysis and alt text generation
        </p>
      </div>

      <div class="setting-item" v-if="settings.enableImageOptimization && settings.enableAutoAltText">
        <label class="setting-label">AI Service API Key</label>
        <input
          type="password"
          v-model="settings.aiServiceApiKey"
          @input="updateSettings"
          class="setting-input"
          placeholder="Enter API key"
        />
        <p class="setting-description">
          API key for AI-powered alt text generation service
        </p>
      </div>

      <div class="setting-item" v-if="settings.enableImageOptimization">
        <label class="setting-label">Image Quality</label>
        <select
          v-model="settings.imageQuality"
          @change="updateSettings"
          class="setting-select"
        >
          <option value="high">High (Best quality)</option>
          <option value="medium">Medium (Balanced)</option>
          <option value="low">Low (Smaller files)</option>
        </select>
        <p class="setting-description">
          Set image optimization quality level
        </p>
      </div>

      <div class="setting-item" v-if="settings.enableImageOptimization && settings.enableDynamicOGImages">
        <label class="setting-label">OG Image Width</label>
        <input
          type="number"
          v-model.number="settings.ogImageWidth"
          @input="updateSettings"
          class="setting-input"
          min="800"
          max="2000"
        />
        <p class="setting-description">
          Width for generated Open Graph images (pixels)
        </p>
      </div>

      <div class="setting-item" v-if="settings.enableImageOptimization && settings.enableDynamicOGImages">
        <label class="setting-label">OG Image Height</label>
        <input
          type="number"
          v-model.number="settings.ogImageHeight"
          @input="updateSettings"
          class="setting-input"
          min="400"
          max="1200"
        />
        <p class="setting-description">
          Height for generated Open Graph images (pixels)
        </p>
      </div>

      <div class="setting-item" v-if="settings.enableImageOptimization && settings.enableLazyLoading">
        <label class="setting-label">Lazy Loading Threshold</label>
        <select
          v-model="settings.lazyLoadingThreshold"
          @change="updateSettings"
          class="setting-select"
        >
          <option value="100">100px</option>
          <option value="200">200px</option>
          <option value="300">300px</option>
          <option value="500">500px</option>
        </select>
        <p class="setting-description">
          Distance from viewport to start loading images (pixels)
        </p>
      </div>
    </div>

    <div class="panel-footer">
      <button @click="testImageOptimization" class="test-button">
        Test Image Optimization
      </button>
      <p class="test-description">
        Click to test the image optimization features with sample images
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'

interface ImageOptimizationSettings {
  enableImageOptimization: boolean
  enableAutoAltText: boolean
  enableImageFileOptimization: boolean
  enableDynamicOGImages: boolean
  enableLazyLoading: boolean
  aiServiceApiKey: string
  aiServiceProvider: string
  imageQuality: string
  ogImageWidth: number
  ogImageHeight: number
  lazyLoadingThreshold: number
}

const settings = reactive<ImageOptimizationSettings>({
  enableImageOptimization: true,
  enableAutoAltText: true,
  enableImageFileOptimization: true,
  enableDynamicOGImages: true,
  enableLazyLoading: true,
  aiServiceApiKey: '',
  aiServiceProvider: 'openai',
  imageQuality: 'high',
  ogImageWidth: 1200,
  ogImageHeight: 630,
  lazyLoadingThreshold: 200
})

const updateSettings = () => {
  // TODO: Implement settings update logic
  console.log('Settings updated:', settings)
}

const testImageOptimization = () => {
  // TODO: Implement test functionality
  console.log('Testing image optimization features...')
}
</script>

<style scoped>
.image-optimization-panel {
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.panel-header {
  margin-bottom: 24px;
}

.panel-header h3 {
  margin: 0 0 8px 0;
  color: #1f2937;
  font-size: 18px;
  font-weight: 600;
}

.panel-header p {
  margin: 0;
  color: #6b7280;
  font-size: 14px;
}

.settings-grid {
  display: grid;
  gap: 20px;
}

.setting-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.setting-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
  color: #374151;
  cursor: pointer;
}

.setting-description {
  margin: 0;
  color: #6b7280;
  font-size: 13px;
  line-height: 1.4;
}

.setting-input,
.setting-select {
  padding: 8px 12px;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  font-size: 14px;
  width: 100%;
  max-width: 300px;
}

.setting-input:focus,
.setting-select:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1);
}

.panel-footer {
  margin-top: 32px;
  padding-top: 20px;
  border-top: 1px solid #e5e7eb;
}

.test-button {
  padding: 10px 16px;
  background: #3b82f6;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s;
}

.test-button:hover {
  background: #2563eb;
}

.test-description {
  margin: 8px 0 0 0;
  color: #6b7280;
  font-size: 13px;
}
</style>
