package moze_intel.projecte.utils;

public final class SoundHandler
{

    public static void init()
    {
        ReflectionHelper.registerSound("projecte:item.pewindmagic");
        ReflectionHelper.registerSound("projecte:item.pewatermagic");
        ReflectionHelper.registerSound("projecte:item.pepower");
        ReflectionHelper.registerSound("projecte:item.peheal");
        ReflectionHelper.registerSound("projecte:item.pedestruct");
        ReflectionHelper.registerSound("projecte:item.pecharge");
        ReflectionHelper.registerSound("projecte:item.peuncharge");
        ReflectionHelper.registerSound("projecte:item.petransmute");
    }

    private SoundHandler() {}

}
