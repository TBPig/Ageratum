package dev.anvilcraft.resource.ageratum.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.logging.LogUtils;
import dev.anvilcraft.resource.ageratum.Ageratum;
import dev.anvilcraft.resource.ageratum.client.feat.markdown.GuideDocumentCache;
import dev.anvilcraft.resource.ageratum.client.feat.markdown.GuideDocumentLoader;
import dev.anvilcraft.resource.ageratum.client.feat.markdown.MDDocument;
import dev.anvilcraft.resource.ageratum.client.feat.markdown.MarkdownParser;
import dev.anvilcraft.resource.ageratum.client.gui.GuideScreen;
import dev.anvilcraft.resource.ageratum.client.registries.AgeratumRegistries;
import dev.anvilcraft.resource.ageratum.client.rendering.text.ttf.TtfFontLoader;
import dev.anvilcraft.resource.ageratum.client.rendering.text.ttf.TtfTextRenderer;
import dev.anvilcraft.resource.ageratum.client.registries.BuiltinExtensionComponents;
import dev.anvilcraft.resource.ageratum.client.registries.BuiltinInlineStyleParsers;
import dev.anvilcraft.resource.ageratum.client.registries.BuiltinRecipeComponentFactories;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import org.slf4j.Logger;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import javax.annotation.Nullable;

@Mod(value = Ageratum.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = Ageratum.MOD_ID, value = Dist.CLIENT)
public class AgeratumClient {
    /**
     * 模组日志记录器。
     */
    private static final Logger LOGGER = LogUtils.getLogger();
    private static TtfFontLoader fontLoader;
    private static TtfFontLoader specialFontLoader;
    private static TtfTextRenderer renderer;

    /**
     * 模组客户端侧构造函数，由 NeoForge 在加载时调用。
     *
     * @param modEventBus  模组专属事件总线
     * @param modContainer 模组容器
     */
    public AgeratumClient(IEventBus modEventBus, ModContainer modContainer) {
        // 注册自定义注册表
        AgeratumRegistries.register(modEventBus);
        // 触发内置扩展组件注册项的类加载
        BuiltinExtensionComponents.init();
        // 触发内置行内样式解析器注册项的类加载
        BuiltinInlineStyleParsers.init();
        // 触发内置配方组件解析器注册项的类加载
        BuiltinRecipeComponentFactories.init();

        RenderSystem.recordRenderCall(() -> {
            fontLoader = new TtfFontLoader(Ageratum.location("font/noto_sans_sc_regular.ttf"));
            specialFontLoader = new TtfFontLoader(Ageratum.location("font/jb_mono_nerd.ttf"));
            renderer = new TtfTextRenderer();
        });
    }

    /**
     * 获取客户端当前语言代码。
     *
     * <p>若无法读取语言管理器，回退到 {@code en_us}。</p>
     */
    private static String getClientLanguageCode(Minecraft minecraft) {
        try {
            return minecraft.getLanguageManager().getSelected();
        } catch (RuntimeException exception) {
            LOGGER.warn("Failed to read client language code, fallback to en_us", exception);
            return GuideDocumentLoader.DEFAULT_LANGUAGE_CODE;
        }
    }

    @SubscribeEvent
    public static void on(RegisterGuiLayersEvent event) {
        event.registerAboveAll(Ageratum.location("test"), ((guiGraphics, deltaTracker) -> {
            PoseStack pose = guiGraphics.pose();
            renderer.addText(
                """
                    Windows PowerShit
                    Copyright (C) Microsoft Corporation. All rights reserved.
                    The quick brown fox jumped over the lazy dog.
                    正在准备Windows
                    请不要关闭你的计算机
                    中国传播家文化的主题餐厅
                    家是本 家是本心灵家港，幸福味道记忆处
                    一群人，一辈子，干好传播家是本文化这件事
                    家是本+传播共识+城市宣传+产品+店面+服务+可复制文化商业模式
                    （具有社会标杆示范作用 自带两大永久生命力的大流量内容）
                    新形势、新商业，新方向
                    新模式、新机遇，新选择，新人生
                    就业创业招商的智选 方向大于努力，平台比能力更重要
                    """,
                50,
                0,
                150,
                1,
                pose,
                -1,
                fontLoader
            );
            renderer.addText(
                """
                    Windows PowerShell
                    Copyright (C) Microsoft Corporation. All rights reserved.
                    
                    Install the latest PowerShell for new features and improvements! https://aka.ms/PSWindows
                    
                    Loading personal and system profiles took 1879ms.
                    
                    Ageratum on  releases/1.21.1 [!+?] via 🅶 v8.8 via ☕ v21.0.5
                    ❯ The quick brown fox jumped over the lazy dog.
                    The : The term 'The' is not recognized as the name of a cmdlet, function, script file, or operable program.
                    Check the spelling of the name, or if a path was included, verify that the path is correct and try again.
                    At line:1 char:1
                    + The quick brown fox jumped over the lazy dog.
                    + ~~~
                        + CategoryInfo          : ObjectNotFound: (The:String) [], CommandNotFoundException
                        + FullyQualifiedErrorId : CommandNotFoundException
                    """,
                50,
                200,
                150,
                0.45f,
                pose,
                -1,
                specialFontLoader
            );
            renderer.addText(
                """
                    argument scale 0.6 ->
                    お別れしたのはもっと   感觉与你分别
                    前の事だったような     已是很久之前的事
                    悲しい光は封じ込めて   我封锁起悲伤的时光　
                    踵すり減らしたんだ     磨平了鞋跟走到现在
                    君といた時は見えた     与你在一起时看见了　
                    今は見えなくなった     如今却已无法再看见
                    透明な彗星をぼんやりと  透明的彗星朦胧地闪烁着
                    でもそれだけ探している  但我只是追寻着它的影子
                    """,
                350,
                0,
                150,
                0.6f,
                pose,
                -1,
                fontLoader
            );

            pose.pushPose();
            pose.translate(380, 100, 0);
            pose.scale(0.6f, 0.6f, 1);
            renderer.addText(
                """
                    posestack scale 0.6 ->
                    お別れしたのはもっと   感觉与你分别
                    前の事だったような     已是很久之前的事
                    悲しい光は封じ込めて   我封锁起悲伤的时光　
                    踵すり減らしたんだ     磨平了鞋跟走到现在
                    君といた時は見えた     与你在一起时看见了　
                    今は見えなくなった     如今却已无法再看见
                    透明な彗星をぼんやりと  透明的彗星朦胧地闪烁着
                    でもそれだけ探している  但我只是追寻着它的影子
                    """,
                0,
                0,
                150,
                1f,
                pose,
                -1,
                fontLoader
            );
            pose.popPose();
            renderer.draw();
        }));
    }

    /**
     * 注册客户端命令 {@code /ageratum}。
     *
     * <p>命令格式：</p>
     * <pre>
     *   /ageratum &lt;namespace&gt;               — 打开该命名空间的 index.md
     *   /ageratum &lt;namespace&gt; &lt;file&gt;        — 打开指定文件（不需要 .md 后缀）
     * </pre>
     * <p>两个参数均支持 Tab 补全，仅显示资源包中实际存在的值。</p>
     *
     * @param event 命令注册事件
     */
    @SubscribeEvent
    public static void onCommandRegister(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(
            Commands.literal("ageratum")
                .then(
                    // ── 第一个参数：命名空间 ──────────────────────────
                    Commands.argument("namespace", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            Minecraft minecraft = Minecraft.getInstance();
                            // 枚举资源包中所有含有 ageratum/*.md 的命名空间
                            return SharedSuggestionProvider.suggest(
                                GuideDocumentLoader.listNamespaces(minecraft.getResourceManager(), getClientLanguageCode(minecraft)),
                                builder
                            );
                        })
                        // 仅提供 namespace，file 缺省为 index.md
                        .executes(context -> openGuide(context, StringArgumentType.getString(context, "namespace"), null))
                        .then(
                            // ── 第二个参数（可选）：文件名 ──────────────
                            Commands.argument("file", StringArgumentType.word())
                                .suggests((context, builder) -> {
                                    Minecraft minecraft = Minecraft.getInstance();
                                    String namespace = StringArgumentType.getString(context, "namespace");
                                    // 枚举该命名空间下的所有 .md 文件（返回不含扩展名的相对路径）
                                    return SharedSuggestionProvider.suggest(
                                        GuideDocumentLoader.listFiles(
                                            minecraft.getResourceManager(),
                                            namespace,
                                            getClientLanguageCode(minecraft)
                                        ),
                                        builder
                                    );
                                })
                                .executes(context -> openGuide(
                                    context,
                                    StringArgumentType.getString(context, "namespace"),
                                    StringArgumentType.getString(context, "file")
                                ))
                        )
                ));
    }

    /**
     * 注册客户端资源重载监听器。
     */
    @SubscribeEvent
    public static void onReloadListenerRegister(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(GuideDocumentCache.reloadListener());
    }

    /**
     * 解析命令参数并打开对应的文档界面。
     *
     * <p>若目标文件不存在，向命令发起方发送错误反馈，不打开界面。</p>
     *
     * @param context      命令执行上下文
     * @param namespace    文档所在的资源包命名空间
     * @param fileArgument 文件名参数（可为 {@code null}，此时使用 index.md）
     * @return 命令执行结果码：1 表示成功，0 表示失败
     */
    private static int openGuide(CommandContext<CommandSourceStack> context, String namespace, @Nullable String fileArgument) {
        Minecraft minecraft = Minecraft.getInstance();

        String languageCode = getClientLanguageCode(minecraft);

        // 将 namespace + languageCode + fileArgument 解析为存在的 ResourceLocation（带回退）
        ResourceLocation documentLocation;
        try {
            Optional<ResourceLocation> resolved = GuideDocumentLoader.resolveExistingLocation(
                minecraft.getResourceManager(),
                namespace,
                languageCode,
                fileArgument
            );
            if (resolved.isEmpty()) {
                context.getSource().sendFailure(Component.literal(
                    "Guide file not found for language '" + languageCode + "'."
                ));
                return 0;
            }
            documentLocation = resolved.get();
        } catch (RuntimeException exception) {
            context.getSource().sendFailure(Component.literal("Invalid guide path."));
            return 0;
        }
        if (!openGuideOnClient(documentLocation, List.of())) {
            context.getSource().sendFailure(
                Component.literal(
                    "Guide file not found: assets/" + documentLocation.getNamespace() + "/" + documentLocation.getPath()
                )
            );
            return 0;
        }
        return 1;
    }

    /**
     * 客户端本地打开文档；若不存在则返回 false。
     */
    public static boolean openGuideOnClient(ResourceLocation location, List<ResourceLocation> breadCrumbs) {
        return openGuideOnClient(location, null, breadCrumbs);
    }

    /**
     * 客户端本地打开文档，可选指定锚点；若不存在则返回 false。
     *
     * @param location 文档资源位置
     * @param anchor   目标锚点（可为 null）
     */
    public static boolean openGuideOnClient(ResourceLocation location, @Nullable String anchor, List<ResourceLocation> breadCrumbs) {
        Minecraft minecraft = Minecraft.getInstance();
        ResourceManager resourceManager = minecraft.getResourceManager();
        if (!GuideDocumentLoader.exists(resourceManager, location)) {
            return false;
        }

        int inheritedLabelScrollRows = 0;
        double inheritedLabelScrollRemainder = 0.0d;
        if (minecraft.screen instanceof GuideScreen currentGuideScreen) {
            inheritedLabelScrollRows = currentGuideScreen.getLabelScrollRows();
            inheritedLabelScrollRemainder = currentGuideScreen.getLabelScrollRemainder();
        }

        // 优先使用预解析缓存，缺失时回退为即时解析
        Optional<MDDocument> cachedDocument = GuideDocumentCache.getParsedDocument(location);
        if (cachedDocument.isPresent()) {
            GuideScreen screen = new GuideScreen(location, cachedDocument.get().components(), breadCrumbs);
            screen.setAnchor(anchor);
            screen.setLabelScrollState(inheritedLabelScrollRows, inheritedLabelScrollRemainder);
            minecraft.setScreen(screen);
            return true;
        }

        String content = GuideDocumentLoader.read(resourceManager, location);
        MDDocument parsedDocument = new MarkdownParser().parseDocument(location, content);
        GuideScreen screen = new GuideScreen(location, parsedDocument.components(), breadCrumbs);
        screen.setAnchor(anchor);
        screen.setLabelScrollState(inheritedLabelScrollRows, inheritedLabelScrollRemainder);
        minecraft.setScreen(screen);
        return true;
    }
}
