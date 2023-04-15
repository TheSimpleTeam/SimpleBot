package net.thesimpleteam.simplebot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.thesimpleteam.simplebot.enums.CommandCategories;

import java.util.Locale;

public class TestCommand extends Command {

    public TestCommand() {
        this.category = CommandCategories.MISC.category;
        this.help = "help.test";
        this.cooldown = 5;
        this.name = "test";
        this.aliases = new String[]{"t", "te", "tes"};
        this.ownerCommand = true;
        this.hidden = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        String list = "pc(UnitType.LENGTH, 3.0856775814913675E32D, \"text.maths.convert.parsec\"),\n" +
                "        Ym(UnitType.LENGTH, 1.0E24D, \"text.maths.convert.yottameter\"),\n" +
                "        Zm(UnitType.LENGTH, 1.0E21D, \"text.maths.convert.zettameter\"),\n" +
                "        ly(UnitType.LENGTH, 9.4607304725808E20D, \"text.maths.convert.lightYear\"),\n" +
                "        Em(UnitType.LENGTH, 1.0E18D, \"text.maths.convert.exameter\"),\n" +
                "        Pm(UnitType.LENGTH, 1.0E15D, \"text.maths.convert.petameter\"),\n" +
                "        Tm(UnitType.LENGTH, 1.0E12D, \"text.maths.convert.terameter\"),\n" +
                "        au(UnitType.LENGTH, 1.495978707E11D, \"text.maths.convert.astronomicalUnit\"),\n" +
                "        Gm(UnitType.LENGTH, 1.0E9D, \"text.maths.convert.gigameter\"),\n" +
                "        Mm(UnitType.LENGTH, 1000000.0D, \"text.maths.convert.megameter\"),\n" +
                "        mam(UnitType.LENGTH, 10000.0D, \"text.maths.convert.myriameter\"),\n" +
                "        lg(UnitType.LENGTH, 4828.032D, \"text.maths.convert.league\"),\n" +
                "        NM(UnitType.LENGTH, 1852.0D, \"text.maths.convert.nauticalMile\"),\n" +
                "        mi(UnitType.LENGTH, 1609.344D, \"text.maths.convert.mile\"),\n" +
                "        km(UnitType.LENGTH, 1000.0D, \"text.maths.convert.kilometer\"),\n" +
                "        fur(UnitType.LENGTH, 201.16840233680466D, \"text.maths.convert.furlong\"),\n" +
                "        hm(UnitType.LENGTH, 100.0D, \"text.maths.convert.hectometer\"),\n" +
                "        ch(UnitType.LENGTH, 20.116840233680467D, \"text.maths.convert.chain\"),\n" +
                "        dam(UnitType.LENGTH, 10.0D, \"text.maths.convert.decameter\"),\n" +
                "        ro(UnitType.LENGTH, 5.0292D, \"text.maths.convert.rod\"),\n" +
                "        fhm(UnitType.LENGTH, 1.8288D, \"text.maths.convert.fathom\"),\n" +
                "        ell(UnitType.LENGTH, 1.143D, \"text.maths.convert.ell\"),\n" +
                "        m(UnitType.LENGTH, 1.0D, \"text.maths.convert.meter\"),\n" +
                "        yd(UnitType.LENGTH, 0.9144D, \"text.maths.convert.yard\"),\n" +
                "        ft(UnitType.LENGTH, 0.3048D, \"text.maths.convert.foot\"),\n" +
                "        sp(UnitType.LENGTH, 0.2286D, \"text.maths.convert.span\"),\n" +
                "        nasp(UnitType.LENGTH, 0.2032D, \"text.maths.convert.naturalSpan\"),\n" +
                "        lnk(UnitType.LENGTH, 0.20116840233680466D, \"text.maths.convert.link\"),\n" +
                "        st(UnitType.LENGTH, 0.1524D, \"text.maths.convert.shaftment\"),\n" +
                "        ha(UnitType.LENGTH, 0.1016D, \"text.maths.convert.hand\"),\n" +
                "        dm(UnitType.LENGTH, 0.1D, \"text.maths.convert.decimeter\"),\n" +
                "        pose(UnitType.LENGTH, 0.088194D, \"text.maths.convert.poppyseed\"),\n" +
                "        plm(UnitType.LENGTH, 0.0762D, \"text.maths.convert.palm\"),\n" +
                "        na(UnitType.LENGTH, 0.05715D, \"text.maths.convert.nail\"),\n" +
                "        in(UnitType.LENGTH, 0.0254D, \"text.maths.convert.inch\"),\n" +
                "        fg(UnitType.LENGTH, 0.022225D, \"text.maths.convert.finger\"),\n" +
                "        dg(UnitType.LENGTH, 0.01905D, \"text.maths.convert.digit\"),\n" +
                "        cm(UnitType.LENGTH, 0.01D, \"text.maths.convert.centimeter\"),\n" +
                "        bc(UnitType.LENGTH, 0.008466666D, \"text.maths.convert.barleycorn\"),\n" +
                "        pa(UnitType.LENGTH, 0.004233333D, \"text.maths.convert.pica\"),\n" +
                "        lin(UnitType.LENGTH, 0.002116D, \"text.maths.convert.line\"),\n" +
                "        mm(UnitType.LENGTH, 0.001D, \"text.maths.convert.millimeter\"),\n" +
                "        pt(UnitType.LENGTH, 3.527778E-4D, \"text.maths.convert.picaPoint\"),\n" +
                "        dmm(UnitType.LENGTH, 1.0E-4D, \"text.maths.convert.decimillimeter\"),\n" +
                "        mil(UnitType.LENGTH, 2.54E-5D, \"text.maths.convert.mil\"),\n" +
                "        cmm(UnitType.LENGTH, 1.0E-5D, \"text.maths.convert.centimillimeter\"),\n" +
                "        µm(UnitType.LENGTH, 1.0E-6D, \"text.maths.convert.micrometer\"),\n" +
                "        nm(UnitType.LENGTH, 1.0E-9D, \"text.maths.convert.nanometer\"),\n" +
                "        br(UnitType.LENGTH, 5.29177210903E-11D, \"text.maths.convert.bohrradius\"),\n" +
                "        anst(UnitType.LENGTH, 1.0E-10D, \"text.maths.convert.angstrom\"),\n" +
                "        pm(UnitType.LENGTH, 1.0E-12D, \"text.maths.convert.picometer\"),\n" +
                "        tp(UnitType.LENGTH, 1.764E-11D, \"text.maths.convert.twip\"),\n" +
                "        fm(UnitType.LENGTH, 1.0E-15D, \"text.maths.convert.femtometer\"),\n" +
                "        xu(UnitType.LENGTH, 1.0021E-13D, \"text.maths.convert.siegbahn\"),\n" +
                "        am(UnitType.LENGTH, 1.0E-18D, \"text.maths.convert.attometer\"),\n" +
                "        zm(UnitType.LENGTH, 1.0E-21D, \"text.maths.convert.zeptometer\"),\n" +
                "        ym(UnitType.LENGTH, 1.0E-24D, \"text.maths.convert.yoctometer\"),\n" +
                "        Ys(UnitType.TIME, 1.0E24D, \"text.maths.convert.yottasecond\"),\n" +
                "        Zs(UnitType.TIME, 1.0E21D, \"text.maths.convert.zettasecond\"),\n" +
                "        Es(UnitType.TIME, 1.0E18D, \"text.maths.convert.exasecond\"),\n" +
                "        Ps(UnitType.TIME, 1.0E15D, \"text.maths.convert.petasecond\"),\n" +
                "        Ts(UnitType.TIME, 1.0E12D, \"text.maths.convert.terasecond\"),\n" +
                "        my(UnitType.TIME, 3.15576E10D, \"text.maths.convert.millennium\"),\n" +
                "        ky(UnitType.TIME, 3.15576E9D, \"text.maths.convert.century\"),\n" +
                "        Gs(UnitType.TIME, 1.0E9D, \"text.maths.convert.gigasecond\"),\n" +
                "        dy(UnitType.TIME, 3.15576E8D, \"text.maths.convert.decade\"),\n" +
                "        y(UnitType.TIME, 3.15576E7D, \"text.maths.convert.year\"),\n" +
                "        M(UnitType.TIME, 2629800.0D, \"text.maths.convert.month\"),\n" +
                "        Ms(UnitType.TIME, 1000000.0D, \"text.maths.convert.megasecond\"),\n" +
                "        w(UnitType.TIME, 604800.0D, \"text.maths.convert.week\"),\n" +
                "        d(UnitType.TIME, 86400.0D, \"text.maths.convert.day\"),\n" +
                "        h(UnitType.TIME, 3600.0D, \"text.maths.convert.hour\"),\n" +
                "        ks(UnitType.TIME, 1000.0D, \"text.maths.convert.kilosecond\"),\n" +
                "        hs(UnitType.TIME, 100.0D, \"text.maths.convert.hectosecond\"),\n" +
                "        min(UnitType.TIME, 60.0D, \"text.maths.convert.minute\"),\n" +
                "        das(UnitType.TIME, 10.0D, \"text.maths.convert.decasecond\"),\n" +
                "        s(UnitType.TIME, 1.0D, \"text.maths.convert.second\"),\n" +
                "        ds(UnitType.TIME, 0.1D, \"text.maths.convert.decisecond\"),\n" +
                "        t(UnitType.TIME, 0.01666666666D, \"text.maths.convert.tierce\"),\n" +
                "        cs(UnitType.TIME, 0.01D, \"text.maths.convert.centisecond\"),\n" +
                "        ms(UnitType.TIME, 0.001D, \"text.maths.convert.millisecond\"),\n" +
                "        µs(UnitType.TIME, 1.0E-6D, \"text.maths.convert.microsecond\"),\n" +
                "        ns(UnitType.TIME, 1.0E-9D, \"text.maths.convert.nanosecond\"),\n" +
                "        ps(UnitType.TIME, 1.0E-12D, \"text.maths.convert.picosecond\"),\n" +
                "        fs(UnitType.TIME, 1.0E-15D, \"text.maths.convert.femtosecond\"),\n" +
                "        as(UnitType.TIME, 1.0E-18D, \"text.maths.convert.attosecond\"),\n" +
                "        zs(UnitType.TIME, 1.0E-21D, \"text.maths.convert.zeptosecond\"),\n" +
                "        ys(UnitType.TIME, 1.0E-24D, \"text.maths.convert.yoctosecond\"),";
        StringBuilder stringBuilder = new StringBuilder();
        for(String part : list.split("\n")){
            stringBuilder.append(texting(part));
        }
        System.out.println(stringBuilder);
        /*String[] args = event.getArgs().split("\\s+");
        List<String> list = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        StringBuilder stringB = new StringBuilder();
        stringB.append(MessageHelper.translateMessage("information.maths", event));
        for(MathsCommand.UnitType unitType : MathsCommand.UnitType.values()){
            for(int index = -1; index <= Arrays.stream(MathsCommand.Unit.values()).filter(unit -> unit.unitType == unitType).toList().size() - 1; index++){
                stringB.append(index == -1 ? unitType == MathsCommand.UnitType.LENGTH ? MessageHelper.translateMessage("text.maths.convert.lengthList", event) + "\n" : MessageHelper.translateMessage("text.maths.convert.timeList", event) + "\n" : index == Arrays.stream(MathsCommand.Unit.values()).filter(unit -> unit.unitType == unitType).toList().size() - 1 ? "- **" + Arrays.stream(MathsCommand.Unit.values()).filter(unit -> unit.unitType == unitType).toList().get(index).name() + "** (" + MessageHelper.translateMessage(Arrays.stream(MathsCommand.Unit.values()).filter(unit -> unit.unitType == unitType).toList().get(index).unitName, event) + ").\n" : "- **" + Arrays.stream(MathsCommand.Unit.values()).filter(unit -> unit.unitType == unitType).toList().get(index).name() + "** (" + MessageHelper.translateMessage(Arrays.stream(MathsCommand.Unit.values()).filter(unit -> unit.unitType == unitType).toList().get(index).unitName, event) + ");\n");
            }
        }
        int charactersCount = 0, field = 0;
        for(char c : stringB.toString().toCharArray()){
            stringBuilder.append(c);
            charactersCount++;
            if (field == 0 ? charactersCount == 1024 : charactersCount / field == 1024 || (field == Math.floor(MessageHelper.translateMessage("information.maths", event).toCharArray().length / 1024D) && (charactersCount / 1024D - field) * 1024 == (MessageHelper.translateMessage("information.maths", event).toCharArray().length / 1024D - field) * 1024)) {
                field++;
                list.add(stringBuilder.toString());
                stringBuilder.setLength(0);
            }
        }
        for(String str : list){
            System.out.println(str + "\nHere\n");
        }*/

        /*for(MathsCommand.UnitType unitType : MathsCommand.UnitType.values()) {
            for (int length = -1; length <= Arrays.stream(MathsCommand.Unit.values()).filter(unit -> unit.unitType == unitType).toList().size(); length++) {
                list.add(length == -1 ? unitType == MathsCommand.UnitType.LENGTH ? MessageHelper.translateMessage("text.maths.convert.lengthList", event) + "\n" : MessageHelper.translateMessage("text.maths.convert.timeList", event) + "\n" : length == Arrays.stream(MathsCommand.Unit.values()).filter(unit -> unit.unitType == unitType).toList().size() ? "- **" + Arrays.stream(MathsCommand.Unit.values()).filter(unit -> unit.unitType == unitType).toList().get(length - 1).name() + "** (" + MessageHelper.translateMessage(Arrays.stream(MathsCommand.Unit.values()).filter(unit -> unit.unitType == unitType).toList().get(length - 1).unitName, event) + ")." : "- **" + Arrays.stream(MathsCommand.Unit.values()).filter(unit -> unit.unitType == unitType).toList().get(length - 1).name() + "** (" + MessageHelper.translateMessage(Arrays.stream(MathsCommand.Unit.values()).filter(unit -> unit.unitType == unitType).toList().get(length - 1).unitName, event) + ");");
            }
        }
        int charactersCount = 0;
        for(String listPart : list){
            if(listPart.length() + charactersCount <= 1024){
                charactersCount += listPart.length();
                stringBuilder.append(listPart);
            } else {
                charactersCount = 0;

                //add an embed
                stringBuilder.setLength(0);
            }
        }
        if(str.length() > 1024){
            int charactersCount = 0;
            List<String> list = new ArrayList<>();
            for(char c : str.toCharArray()){
                stringBuilder.append(c);
                charactersCount++;
                if(charactersCount == 1024 || charactersCount == ((str.length() / 1024D) - Math.floor(str.length() / 1024D)) * 1024){
                    list.add(stringBuilder.toString());
                    charactersCount = 0;
                    stringBuilder.setLength(0);
                }
            }
            for(int i = 0; i < Math.ceil(str.length() / 1024D); i++){
                embedBuilder.addField(i == 0 ? "__" + MessageHelper.translateMessage("text.commands.syntaxError.informations", event) + "__" : "", list.get(i), false);
            }
        } else embedBuilder.addField("__" + MessageHelper.translateMessage("text.commands.syntaxError.informations", event) + "__", str, false);
        event.reply(embedBuilder.build());*/

    }

    public static String texting(String text) {
        return text.split("text.maths.convert.")[1].split("\"")[0].toUpperCase(Locale.ROOT) + "(" + text.split("\\(")[1].split("\"text.maths.convert.")[0] + "\"" + text.split("\\(")[0].strip() + "\"),\n";
    }
}
