package cn.ksmcbrigade.JL;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;

@Mod("jl")
@Mod.EventBusSubscriber
public class JarLoader {

    public static ModJarLoader classLoader = null;

    public static ArrayList<URL> urls = new ArrayList<>();

    public JarLoader() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public static void RegisterCommands(RegisterCommandsEvent event){
        event.getDispatcher().register(Commands.literal("loaderJar").then(Commands.argument("path", StringArgumentType.string()).executes(args -> {
            try {
                File jarFile = new File(StringArgumentType.getString(args,"path"));
                urls.add(jarFile.toURI().toURL());
                if(classLoader==null){
                    classLoader = new ModJarLoader(urls.toArray(new URL[urls.size()]));
                }
                classLoader.add(jarFile.toURI().toURL());
                args.getSource().getEntity().sendMessage(Component.nullToEmpty("Done."),args.getSource().getEntity().getUUID());
            } catch (Exception e) {
                System.out.println("Failed in load jar file.");
                e.printStackTrace();
            }

            return 0;
        })));

        event.getDispatcher().register(Commands.literal("loaderJarAndInvoke").then(Commands.argument("path", StringArgumentType.string()).then(Commands.argument("class", StringArgumentType.string()).then(Commands.argument("function", StringArgumentType.string()).then(Commands.argument("args", StringArgumentType.string()).executes(args -> {
            try {
                String returnText;
                String clazz = StringArgumentType.getString(args,"class");
                String function = StringArgumentType.getString(args,"function");
                String Args = StringArgumentType.getString(args,"args");
                File jarFile = new File(StringArgumentType.getString(args,"path"));
                urls.add(jarFile.toURI().toURL());
                if(classLoader==null){
                    classLoader = new ModJarLoader(urls.toArray(new URL[urls.size()]));
                }
                classLoader.add(jarFile.toURI().toURL());

                Class<?> cls = Class.forName(clazz, true, classLoader);

                Method method = cls.getMethod(function);

                Object instance = cls.newInstance();

                if(Args.equals("null")){
                    returnText = String.valueOf(method.invoke(instance));
                }
                else{
                    returnText = String.valueOf(method.invoke(instance,Args.split(",")));
                }

                args.getSource().getEntity().sendMessage(Component.nullToEmpty("Return:"),args.getSource().getEntity().getUUID());

                for(String returns : returnText.split("\n")){
                    args.getSource().getEntity().sendMessage(Component.nullToEmpty(returns),args.getSource().getEntity().getUUID());
                }

                args.getSource().getEntity().sendMessage(Component.nullToEmpty("Done."),args.getSource().getEntity().getUUID());
            } catch (Exception e) {
                System.out.println("Failed in load jar file.");
                e.printStackTrace();
            }

            return 0;
        }))))));
    }
}
