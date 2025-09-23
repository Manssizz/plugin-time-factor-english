# TimeFactor SEO Plugin

TimeFactor SEO is a comprehensive SEO optimization plugin designed specifically for the Halo blog system. It dynamically injects structured data (meta/script tags) to improve your site's visibility and ranking performance across major search engines.

## 🌐 Demo & Community

- **Demo Site**: [https://www.isiotak.com/](https://www.isiotak.com/)

## ✨ Key Features

### 🎯 Intelligent SEO Injection
- **Multi-Platform Support**: Supports structured data formats for Google, Baidu, ByteDance, OG, and other mainstream search engines
- **Dynamic Content**: Automatically fills titles, descriptions, authors, tags, and other fields based on page type and content

### 🔧 Flexible Configuration
- Enable/disable search engine optimization features
- Configurable default cover images
- Automatic site information retrieval (title, logo, keywords, etc.)

### 🛡️ Performance Optimization
- Zero performance impact on regular users
- Compact structured data format without unnecessary comments
- Comprehensive error handling to ensure system stability

## 🔍 Supported Search Engines

- **Google** (Googlebot)
- **Baidu** (Baiduspider)
- **ByteDance** (Bytespider)
- **Bing** (Bingbot)
- **360 Search** (360spider)
- **Sogou** (Sogou)
- **Yandex**
- **DuckDuckGo** (DuckDuckBot)
- **Yahoo** (Slurp)
- **Ask** (Teoma)

## 📊 Structured Data Types

### Open Graph (OG)
- Title, description, cover image
- Author, tags, publication time
- Site information

### Baidu Structured Data
- Article title, summary, cover
- Author, category, publication time
- Site name, logo

### ByteDance Structured Data
- Content title, description, images
- Author information, publication time
- Site identification

### Google JSON-LD (schema.org)
- Article structured data
- Author information
- Publication time (with timezone)

## 🌐 Social Media Optimization

### Twitter Cards
- Summary large image cards
- Enhanced Twitter sharing experience
- **Configurable Twitter Username**: Set your own Twitter handle for proper attribution
- **Smart Fallback**: Uses site name if no username is configured

### LinkedIn Optimization
- Professional content sharing
- LinkedIn-specific meta tags

### Facebook Integration
- Enhanced Facebook sharing
- Facebook App ID support
- Optimized social previews

### Enhanced Social Features
- Multi-language support (Indonesian/English)
- Image optimization for social platforms
- Custom social media descriptions

## 🔧 Advanced SEO Features

### Schema.org Structured Data
- **FAQ Schema**: Automatically detects and generates FAQ structured data
- **How-To Schema**: Creates step-by-step tutorial structured data
- **Breadcrumb Schema**: Generates navigation breadcrumb markup
- **Article Schema**: Complete blog posting structured data

### Content Intelligence
- **Auto Content Detection**: Automatically identifies FAQ, How-To, and tutorial content
- **Multi-language Support**: Supports English and Indonesian content patterns
- **Smart Extraction**: Extracts Q&A pairs and step-by-step instructions

### Image Optimization
- **Auto Alt Text Generation**: AI-powered automatic alt text creation
- **Image Filename Optimization**: SEO-friendly filename generation
- **Dynamic OG Images**: Generate social media preview images
- **Lazy Loading**: Performance-optimized image loading
- **Fetch Priority High**: Prioritized loading for cover images
- **Image Quality Control**: Configurable quality settings
- **Size Optimization**: Maximum width/height constraints

### Search Engine Integration
- **Auto Push**: Automatic URL submission to search engines
- **Google Search Console**: Direct integration with Google Webmaster Tools
- **Bing Webmaster Tools**: Automated submission to Bing
- **Baidu Webmaster Tools**: Chinese search engine integration
- **Sitemap Management**: Automatic sitemap URL handling

## 📈 Analytics & Monitoring

### Google Analytics 4
- Complete GA4 integration
- Enhanced tracking capabilities
- Custom event monitoring

### Search Console Integration
- Real-time indexing status
- Performance metrics tracking
- Search query analytics

### Core Web Vitals
- Performance monitoring
- User experience metrics
- PageSpeed Insights integration

### SEO Dashboard
- Comprehensive performance metrics
- Visual analytics dashboard
- Real-time SEO monitoring

## 🛠️ Technical Features

### Performance Optimizations
- **Zero Overhead**: No impact on regular user experience
- **Efficient Processing**: Optimized structured data generation
- **Error Handling**: Robust error management and logging
- **Caching**: Intelligent caching for better performance

### Developer Experience
- **Easy Configuration**: User-friendly settings interface
- **Comprehensive Logging**: Detailed operation logs
- **Debug Support**: Development and troubleshooting tools
- **API Integration**: RESTful API for external integrations

## 🚀 Installation & Setup

### System Requirements
- Java 21+
- Node.js 18+
- pnpm

### Development Setup

```bash
# Build the plugin
./gradlew build

# Frontend development
cd ui
pnpm install
pnpm dev
```

### Production Build

```bash
./gradlew build
```

The built plugin jar file will be available in the `build/libs` directory.

## 📋 Configuration Options

### Basic SEO Settings
- Enable/disable various search engine optimizations
- Configure robots meta tags
- Set canonical URLs
- Twitter Card integration with configurable username
- Custom Twitter handle for proper attribution

### Social Media Settings
- Social platform optimizations
- Image optimization settings
- Facebook App ID configuration
- Multi-language support

### Advanced Settings
- Search engine auto-push configuration
- API key management
- Schema.org settings
- Performance optimizations

### Analytics Settings
- Google Analytics 4 setup
- Search Console integration
- Performance monitoring
- Dashboard configuration

## 🔒 Security & Privacy

- **No External Dependencies**: All processing happens locally
- **Privacy Focused**: No data sent to third-party services
- **Secure Configuration**: Encrypted API key storage
- **GDPR Compliant**: No tracking of personal data

## 🌍 Multi-language Support

- **English**: Full English interface and documentation
- **Indonesian**: Complete localization support
- **Chinese**: Traditional Chinese interface
- **Extensible**: Easy to add new language support

## 📞 Support & Community

- **Documentation**: Comprehensive setup and usage guides
- **Community**: Active user community and forums
- **Support**: Professional technical support available
- **Bug Reports**: GitHub issue tracking system

## 📄 License

[GPL-3.0](./LICENSE) © [Handsome](https://github.com/somehand)

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guide](CONTRIBUTING.md) for details on how to get involved.

### Development Roadmap
- [ ] Enhanced AI-powered content analysis
- [ ] Advanced image recognition for alt text
- [ ] Real-time SEO scoring and recommendations
- [ ] Multi-site management capabilities
- [ ] Advanced analytics and reporting features

---

**TimeFactor SEO** - Elevate your Halo blog's search engine presence with comprehensive, intelligent SEO optimization.
