<template>
  <div class="seo-dashboard">
    <h2>SEO Performance Dashboard</h2>

    <div class="dashboard-grid">
      <!-- Core Web Vitals -->
      <div class="metric-card">
        <h3>Core Web Vitals</h3>
        <div class="metrics">
          <div class="metric">
            <span class="label">LCP</span>
            <span class="value" :class="getScoreClass(coreWebVitals.lcp, 'lcp')">
              {{ coreWebVitals.lcp ? coreWebVitals.lcp + 's' : 'N/A' }}
            </span>
          </div>
          <div class="metric">
            <span class="label">FID</span>
            <span class="value" :class="getScoreClass(coreWebVitals.fid, 'fid')">
              {{ coreWebVitals.fid ? coreWebVitals.fid + 'ms' : 'N/A' }}
            </span>
          </div>
          <div class="metric">
            <span class="label">CLS</span>
            <span class="value" :class="getScoreClass(coreWebVitals.cls, 'cls')">
              {{ coreWebVitals.cls || 'N/A' }}
            </span>
          </div>
        </div>
      </div>

      <!-- SEO Performance -->
      <div class="metric-card">
        <h3>SEO Performance</h3>
        <div class="metrics">
          <div class="metric">
            <span class="label">Organic Traffic</span>
            <span class="value">{{ formatNumber(seoMetrics.organicTraffic) }}</span>
          </div>
          <div class="metric">
            <span class="label">Organic Keywords</span>
            <span class="value">{{ formatNumber(seoMetrics.organicKeywords) }}</span>
          </div>
          <div class="metric">
            <span class="label">Backlinks</span>
            <span class="value">{{ formatNumber(seoMetrics.backlinks) }}</span>
          </div>
          <div class="metric">
            <span class="label">Domain Authority</span>
            <span class="value">{{ seoMetrics.domainAuthority }}/100</span>
          </div>
        </div>
      </div>

      <!-- Indexing Status -->
      <div class="metric-card">
        <h3>Indexing Status</h3>
        <div class="metrics">
          <div class="metric">
            <span class="label">Indexed Pages</span>
            <span class="value">{{ formatNumber(indexingStatus.indexed) }}</span>
          </div>
          <div class="metric">
            <span class="label">Not Indexed</span>
            <span class="value">{{ formatNumber(indexingStatus.notIndexed) }}</span>
          </div>
          <div class="metric">
            <span class="label">Crawled (Not Indexed)</span>
            <span class="value">{{ formatNumber(indexingStatus.crawledNotIndexed) }}</span>
          </div>
        </div>
      </div>

      <!-- Performance Scores -->
      <div class="metric-card">
        <h3>Performance Scores</h3>
        <div class="metrics">
          <div class="metric">
            <span class="label">Page Speed</span>
            <span class="value" :class="getScoreClass(seoMetrics.pageSpeedScore, 'score')">
              {{ seoMetrics.pageSpeedScore }}/100
            </span>
          </div>
          <div class="metric">
            <span class="label">Mobile Friendly</span>
            <span class="value" :class="getScoreClass(seoMetrics.mobileFriendlyScore, 'score')">
              {{ seoMetrics.mobileFriendlyScore }}/100
            </span>
          </div>
        </div>
      </div>
    </div>

    <div class="actions">
      <button @click="refreshData" :disabled="loading">
        {{ loading ? 'Refreshing...' : 'Refresh Data' }}
      </button>
      <button @click="exportReport">
        Export Report
      </button>
    </div>
  </div>
</template>

<script>
export default {
  name: "SEOPerformanceDashboard",
  data() {
    return {
      coreWebVitals: {},
      seoMetrics: {},
      indexingStatus: {},
      loading: false,
    };
  },
  mounted() {
    this.loadDashboardData();
  },
  methods: {
    async loadDashboardData() {
      this.loading = true;
      try {
        // Load Core Web Vitals
        const cwvResponse = await fetch('/apis/api.plugin.halo.run/v1alpha1/timefactor/analytics/core-web-vitals?url=' + encodeURIComponent(window.location.origin));
        if (cwvResponse.ok) {
          this.coreWebVitals = await cwvResponse.json();
        }

        // Load SEO Performance Metrics
        const seoResponse = await fetch('/apis/api.plugin.halo.run/v1alpha1/timefactor/analytics/seo-performance');
        if (seoResponse.ok) {
          this.seoMetrics = await seoResponse.json();
        }

        // Load Indexing Status
        const indexResponse = await fetch('/apis/api.plugin.halo.run/v1alpha1/timefactor/analytics/indexing-status');
        if (indexResponse.ok) {
          this.indexingStatus = await indexResponse.json();
        }
      } catch (error) {
        console.error('Failed to load dashboard data:', error);
      } finally {
        this.loading = false;
      }
    },

    getScoreClass(value, type) {
      if (!value) return '';

      switch (type) {
        case 'lcp':
          return value <= 2.5 ? 'good' : value <= 4 ? 'needs-improvement' : 'poor';
        case 'fid':
          return value <= 100 ? 'good' : value <= 300 ? 'needs-improvement' : 'poor';
        case 'cls':
          return value <= 0.1 ? 'good' : value <= 0.25 ? 'needs-improvement' : 'poor';
        case 'score':
          return value >= 90 ? 'good' : value >= 50 ? 'needs-improvement' : 'poor';
        default:
          return '';
      }
    },

    formatNumber(num) {
      if (!num) return '0';
      return num.toLocaleString();
    },

    async refreshData() {
      await this.loadDashboardData();
    },

    exportReport() {
      // TODO: Implement report export functionality
      alert('Export functionality will be implemented');
    },
  },
};
</script>

<style scoped>
.seo-dashboard {
  padding: 1em;
}

.dashboard-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 1em;
  margin-bottom: 2em;
}

.metric-card {
  background: #f9f9f9;
  border-radius: 8px;
  padding: 1em;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.metric-card h3 {
  margin-top: 0;
  color: #333;
}

.metrics {
  display: flex;
  flex-direction: column;
  gap: 0.5em;
}

.metric {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.label {
  font-weight: 500;
  color: #666;
}

.value {
  font-weight: bold;
  font-size: 1.1em;
}

.value.good {
  color: #52c41a;
}

.value.needs-improvement {
  color: #faad14;
}

.value.poor {
  color: #ff4d4f;
}

.actions {
  display: flex;
  gap: 1em;
  justify-content: center;
}

button {
  background-color: #1890ff;
  color: white;
  border: none;
  padding: 0.5em 1em;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9em;
}

button:hover:not(:disabled) {
  background-color: #40a9ff;
}

button:disabled {
  background-color: #d9d9d9;
  cursor: not-allowed;
}
</style>
