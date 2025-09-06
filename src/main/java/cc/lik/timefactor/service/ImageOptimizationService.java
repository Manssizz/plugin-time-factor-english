package cc.lik.timefactor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import run.halo.app.infra.ExternalLinkProcessor;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageOptimizationService {
    private static final Pattern INVALID_FILENAME_CHARS = Pattern.compile("[^a-zA-Z0-9\\-_]");
    private static final int OG_IMAGE_WIDTH = 1200;
    private static final int OG_IMAGE_HEIGHT = 630;

    private final ExternalLinkProcessor externalLinkProcessor;

    /**
     * Generates automatic alt text for images using basic image analysis
     */
    public Mono<String> generateAltText(String imageUrl, String postTitle) {
        return Mono.fromCallable(() -> {
            try {
                if (imageUrl == null || imageUrl.trim().isEmpty()) {
                    return generateFallbackAltText(postTitle);
                }

                // For now, implement basic alt text generation
                // In a real implementation, this would use AI/ML services
                return generateBasicAltText(imageUrl, postTitle);
            } catch (Exception e) {
                log.warn("Failed to generate alt text for image: {}", imageUrl, e);
                return generateFallbackAltText(postTitle);
            }
        });
    }

    /**
     * Optimizes image file names for SEO
     */
    public Mono<String> optimizeImageFilename(String originalFilename, String postTitle) {
        return Mono.fromCallable(() -> {
            if (originalFilename == null || originalFilename.trim().isEmpty()) {
                return generateOptimizedFilename(postTitle);
            }

            return optimizeExistingFilename(originalFilename, postTitle);
        });
    }

    /**
     * Generates dynamic Open Graph images
     */
    public Mono<String> generateOpenGraphImage(String title, String siteName) {
        return Mono.fromCallable(() -> {
            try {
                BufferedImage image = new BufferedImage(OG_IMAGE_WIDTH, OG_IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = image.createGraphics();

                // Set background
                g2d.setColor(Color.WHITE);
                g2d.fillRect(0, 0, OG_IMAGE_WIDTH, OG_IMAGE_HEIGHT);

                // Set font and color
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.BOLD, 48));

                // Draw title
                FontMetrics fm = g2d.getFontMetrics();
                String displayTitle = title.length() > 30 ? title.substring(0, 27) + "..." : title;
                int titleWidth = fm.stringWidth(displayTitle);
                int titleX = (OG_IMAGE_WIDTH - titleWidth) / 2;
                int titleY = OG_IMAGE_HEIGHT / 2 - 50;
                g2d.drawString(displayTitle, titleX, titleY);

                // Draw site name
                g2d.setFont(new Font("Arial", Font.PLAIN, 24));
                fm = g2d.getFontMetrics();
                int siteWidth = fm.stringWidth(siteName);
                int siteX = (OG_IMAGE_WIDTH - siteWidth) / 2;
                int siteY = OG_IMAGE_HEIGHT / 2 + 50;
                g2d.drawString(siteName, siteX, siteY);

                g2d.dispose();

                // Convert to base64
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(image, "png", baos);
                byte[] imageBytes = baos.toByteArray();
                String base64Image = Base64.getEncoder().encodeToString(imageBytes);

                return "data:image/png;base64," + base64Image;
            } catch (IOException e) {
                log.error("Failed to generate Open Graph image", e);
                return null;
            }
        });
    }

    /**
     * Generates lazy loading HTML attributes
     */
    public String generateLazyLoadingAttributes() {
        return "loading=\"lazy\"";
    }

    /**
     * Analyzes image dimensions and format
     */
    public Mono<ImageMetadata> analyzeImage(String imageUrl) {
        return Mono.fromCallable(() -> {
            try {
                URL url = new URL(imageUrl);
                BufferedImage image = ImageIO.read(url);

                if (image == null) {
                    return new ImageMetadata(0, 0, "unknown", 0);
                }

                return new ImageMetadata(
                    image.getWidth(),
                    image.getHeight(),
                    getImageFormat(imageUrl),
                    image.getWidth() * image.getHeight()
                );
            } catch (Exception e) {
                log.warn("Failed to analyze image: {}", imageUrl, e);
                return new ImageMetadata(0, 0, "unknown", 0);
            }
        });
    }

    private String generateBasicAltText(String imageUrl, String postTitle) {
        // Extract filename from URL
        String filename = extractFilenameFromUrl(imageUrl);

        // Remove file extension and clean up
        if (filename.contains(".")) {
            filename = filename.substring(0, filename.lastIndexOf("."));
        }

        // Convert to readable format
        String altText = filename.replaceAll("[\\-_]", " ").trim();

        // If alt text is too short or generic, use post title
        if (altText.length() < 3 || altText.matches(".*(img|image|photo|picture).*")) {
            return postTitle + " - Featured Image";
        }

        return capitalizeWords(altText);
    }

    private String generateFallbackAltText(String postTitle) {
        return postTitle != null && !postTitle.trim().isEmpty()
            ? postTitle + " - Featured Image"
            : "Featured Image";
    }

    private String optimizeExistingFilename(String filename, String postTitle) {
        // Remove invalid characters
        String cleanFilename = INVALID_FILENAME_CHARS.matcher(filename).replaceAll("-");

        // Convert to lowercase
        cleanFilename = cleanFilename.toLowerCase();

        // Remove multiple consecutive hyphens
        cleanFilename = cleanFilename.replaceAll("-+", "-");

        // Remove leading/trailing hyphens
        cleanFilename = cleanFilename.replaceAll("^-|-$", "");

        // If filename is too short, generate from post title
        if (cleanFilename.length() < 3) {
            return generateOptimizedFilename(postTitle);
        }

        return cleanFilename;
    }

    private String generateOptimizedFilename(String postTitle) {
        if (postTitle == null || postTitle.trim().isEmpty()) {
            return "featured-image";
        }

        // Convert to lowercase and replace spaces with hyphens
        String filename = postTitle.toLowerCase()
            .replaceAll("\\s+", "-")
            .replaceAll("[^a-z0-9\\-]", "");

        // Remove multiple consecutive hyphens
        filename = filename.replaceAll("-+", "-");

        // Remove leading/trailing hyphens
        filename = filename.replaceAll("^-|-$", "");

        // Limit length
        if (filename.length() > 50) {
            filename = filename.substring(0, 50);
            filename = filename.replaceAll("-$", "");
        }

        return filename.isEmpty() ? "featured-image" : filename;
    }

    private String extractFilenameFromUrl(String url) {
        if (url == null) return "";

        try {
            URL urlObj = new URL(url);
            String path = urlObj.getPath();
            if (path.contains("/")) {
                return path.substring(path.lastIndexOf("/") + 1);
            }
            return path;
        } catch (Exception e) {
            // If URL parsing fails, try to extract from the end
            if (url.contains("/")) {
                return url.substring(url.lastIndexOf("/") + 1);
            }
            return url;
        }
    }

    private String getImageFormat(String imageUrl) {
        if (imageUrl == null) return "unknown";

        String lowerUrl = imageUrl.toLowerCase();
        if (lowerUrl.endsWith(".jpg") || lowerUrl.endsWith(".jpeg")) {
            return "jpeg";
        } else if (lowerUrl.endsWith(".png")) {
            return "png";
        } else if (lowerUrl.endsWith(".gif")) {
            return "gif";
        } else if (lowerUrl.endsWith(".webp")) {
            return "webp";
        } else if (lowerUrl.endsWith(".svg")) {
            return "svg";
        }

        return "unknown";
    }

    private String capitalizeWords(String text) {
        if (text == null || text.trim().isEmpty()) {
            return text;
        }

        String[] words = text.split("\\s+");
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < words.length; i++) {
            if (i > 0) result.append(" ");
            String word = words[i];
            if (word.length() > 0) {
                result.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) {
                    result.append(word.substring(1).toLowerCase());
                }
            }
        }

        return result.toString();
    }

    public record ImageMetadata(int width, int height, String format, long pixelCount) {}
}
