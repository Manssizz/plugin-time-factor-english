package cc.lik.seotoolset;

import org.springframework.stereotype.Component;
import run.halo.app.plugin.BasePlugin;
import run.halo.app.plugin.PluginContext;

/**
 * <p>SEO Toolset Plugin main class to manage the lifecycle of the plugin.</p>
 * <p>This class must be public and have a public constructor.</p>
 * <p>Only one main class extending {@link BasePlugin} is allowed per plugin.</p>
 *
 * @author Handsome
 * @since 1.0.0
 */
@Component
public class SeoToolsetPlugin extends BasePlugin {

    public SeoToolsetPlugin(PluginContext pluginContext) {
        super(pluginContext);
    }

    @Override
    public void start() {
        System.out.println("SEO Toolset Plugin started!");
    }

    @Override
    public void stop() {
        System.out.println("SEO Toolset Plugin Stopped");
    }
}
