/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.fml.common.FMLCommonHandler
 *  net.minecraftforge.fml.relauncher.Side
 *  org.lwjgl.input.Mouse
 */
package ic2.core.gui.dynamic;

import ic2.core.gui.EnergyGauge;
import ic2.core.gui.Gauge;
import ic2.core.gui.SlotGrid;
import ic2.core.gui.Text;
import ic2.core.ref.TeBlock;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Mouse;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class GuiParser {
    public static GuiNode parse(TeBlock teBlock) {
        ResourceLocation loc = new ResourceLocation("ic2", "guidef/" + teBlock.getName() + ".xml");
        try {
            return GuiParser.parse(loc, teBlock.getTeClass());
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static GuiNode parse(ResourceLocation location, Class<?> baseClass) throws IOException, ParserConfigurationException, SAXException {
        InputStream is = GuiParser.class.getResourceAsStream("/assets/" + location.getResourceDomain() + "/" + location.getResourcePath());
        try {
            is = new BufferedInputStream(is);
            GuiNode guiNode = GuiParser.parse(is, baseClass);
            return guiNode;
        }
        finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private static GuiNode parse(InputStream is, Class<?> baseClass) throws SAXException, IOException {
        is = new BufferedInputStream(is);
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser parser = factory.newSAXParser();
            XMLReader reader = parser.getXMLReader();
            SaxHandler handler = new SaxHandler(baseClass);
            reader.setContentHandler(handler);
            reader.parse(new InputSource(is));
            return handler.getResult();
        }
        catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getAttr(Attributes attributes, String name) throws SAXException {
        String val = attributes.getValue(name);
        if (val == null) {
            throw new SAXException("missing attribute: " + name);
        }
        return val;
    }

    private static String getAttr(Attributes attributes, String name, String defValue) {
        String val = attributes.getValue(name);
        if (val == null) {
            return defValue;
        }
        return val;
    }

    private static boolean getBoolAttr(Attributes attributes, String name, boolean defValue) throws SAXException {
        String val = attributes.getValue(name);
        if (val == null) {
            return defValue;
        }
        return GuiParser.parseBool(val);
    }

    private static boolean parseBool(String str) throws SAXException {
        if (str.equals("true")) {
            return true;
        }
        if (str.equals("false")) {
            return false;
        }
        throw new SAXException("invalid bool value: " + str);
    }

    private static int getIntAttr(Attributes attributes, String name) throws SAXException {
        String val = attributes.getValue(name);
        if (val == null) {
            throw new SAXException("missing attribute: " + name);
        }
        return GuiParser.parseInt(val);
    }

    private static int getIntAttr(Attributes attributes, String name, int defValue) {
        String val = attributes.getValue(name);
        if (val == null) {
            return defValue;
        }
        return GuiParser.parseInt(val);
    }

    private static int parseInt(String str) {
        if (str.startsWith("#")) {
            return Integer.parseInt(str.substring(1), 16);
        }
        if (str.startsWith("0x")) {
            return Integer.parseInt(str.substring(2), 16);
        }
        return Integer.parseInt(str);
    }

    public static class FluidSlotNode
    extends Node {
        final int x;
        final int y;
        final String name;

        FluidSlotNode(ParentNode parent, Attributes attributes) throws SAXException {
            super(parent);
            this.x = GuiParser.getIntAttr(attributes, "x");
            this.y = GuiParser.getIntAttr(attributes, "y");
            this.name = GuiParser.getAttr(attributes, "name");
        }

        @Override
        public NodeType getType() {
            return NodeType.fluidslot;
        }
    }

    public static class FluidTankNode
    extends Node {
        final int x;
        final int y;
        final String name;

        FluidTankNode(ParentNode parent, Attributes attributes) throws SAXException {
            super(parent);
            this.x = GuiParser.getIntAttr(attributes, "x");
            this.y = GuiParser.getIntAttr(attributes, "y");
            this.name = GuiParser.getAttr(attributes, "name");
        }

        @Override
        public NodeType getType() {
            return NodeType.fluidtank;
        }
    }

    public static class TextNode
    extends Node {
        private static final int defaultColor = 4210752;
        public final int x;
        public final int y;
        public final int width;
        public final int height;
        public final int xOffset;
        public final int yOffset;
        public final boolean centerX;
        public final boolean centerY;
        public final Text.TextAlignment align;
        public final int color;
        public final boolean shadow;
        public TextProvider.ITextProvider text;

        TextNode(ParentNode parent, Attributes attributes) throws SAXException {
            super(parent);
            this.x = GuiParser.getIntAttr(attributes, "x", 0);
            this.y = GuiParser.getIntAttr(attributes, "y", 0);
            this.width = GuiParser.getIntAttr(attributes, "width", -1);
            this.height = GuiParser.getIntAttr(attributes, "height", -1);
            this.xOffset = GuiParser.getIntAttr(attributes, "xoffset", 0);
            this.yOffset = GuiParser.getIntAttr(attributes, "yoffset", 0);
            String alignName = GuiParser.getAttr(attributes, "align", "start");
            this.align = Text.TextAlignment.get(alignName);
            if (this.align == null) {
                throw new SAXException("invalid alignment: " + alignName);
            }
            String center = GuiParser.getAttr(attributes, "center", this.align == Text.TextAlignment.Center ? "x" : "");
            this.centerX = center.indexOf(120) != -1;
            this.centerY = center.indexOf(121) != -1;
            this.color = GuiParser.getIntAttr(attributes, "color", 4210752);
            this.shadow = attributes.getIndex("shadow") != -1;
        }

        @Override
        public NodeType getType() {
            return NodeType.text;
        }

        @Override
        public void setContent(String content) throws SAXException {
            if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
                Mouse.setGrabbed((boolean)false);
            }
            this.text = TextProvider.parse(content, this.parent.getBaseClass());
        }
    }

    public static class SlotGridNode
    extends Node {
        public final int x;
        public final int y;
        public final String name;
        public final int offset;
        public final int rows;
        public final int cols;
        public final boolean vertical;
        public final SlotGrid.SlotStyle style;

        SlotGridNode(ParentNode parent, Attributes attributes) throws SAXException {
            super(parent);
            this.x = GuiParser.getIntAttr(attributes, "x");
            this.y = GuiParser.getIntAttr(attributes, "y");
            this.name = GuiParser.getAttr(attributes, "name");
            this.offset = GuiParser.getIntAttr(attributes, "offset", 0);
            this.rows = GuiParser.getIntAttr(attributes, "rows", -1);
            this.cols = GuiParser.getIntAttr(attributes, "cols", -1);
            this.vertical = GuiParser.getBoolAttr(attributes, "vertical", false);
            String styleName = GuiParser.getAttr(attributes, "style", "normal");
            this.style = SlotGrid.SlotStyle.get(styleName);
            if (this.style == null) {
                throw new SAXException("invalid slot style: " + styleName);
            }
        }

        @Override
        public NodeType getType() {
            return NodeType.slotgrid;
        }

        public SlotGridDimension getDimension(int totalSize) {
            totalSize -= this.offset;
            if (!this.vertical) {
                if (this.cols > 0) {
                    return new SlotGridDimension(Math.max(this.rows, (totalSize + this.cols - 1) / this.cols), this.cols);
                }
                if (this.rows > 0) {
                    return new SlotGridDimension(this.rows, (totalSize + this.rows - 1) / this.rows);
                }
                int cols = (int)Math.floor(Math.sqrt(totalSize));
                return new SlotGridDimension((totalSize + cols - 1) / cols, cols);
            }
            if (this.rows > 0) {
                return new SlotGridDimension(this.rows, Math.max(this.cols, (totalSize + this.rows - 1) / this.rows));
            }
            if (this.cols > 0) {
                return new SlotGridDimension((totalSize + this.cols - 1) / this.cols, this.cols);
            }
            int rows = (int)Math.floor(Math.sqrt(totalSize));
            return new SlotGridDimension(rows, (totalSize + rows - 1) / rows);
        }

        public static class SlotGridDimension {
            public final int rows;
            public final int cols;

            public SlotGridDimension(int rows, int cols) {
                this.rows = rows;
                this.cols = cols;
            }
        }

    }

    public static class SlotNode
    extends Node {
        public final int x;
        public final int y;
        public final String name;
        public final int index;
        public final SlotGrid.SlotStyle style;

        SlotNode(ParentNode parent, Attributes attributes) throws SAXException {
            super(parent);
            this.x = GuiParser.getIntAttr(attributes, "x");
            this.y = GuiParser.getIntAttr(attributes, "y");
            this.name = GuiParser.getAttr(attributes, "name");
            this.index = GuiParser.getIntAttr(attributes, "index", 0);
            String styleName = GuiParser.getAttr(attributes, "style", "normal");
            this.style = SlotGrid.SlotStyle.get(styleName);
            if (this.style == null) {
                throw new SAXException("invalid slot style: " + styleName);
            }
        }

        @Override
        public NodeType getType() {
            return NodeType.slot;
        }
    }

    protected static class PlayerInventoryNode
    extends Node {
        final int x;
        final int y;

        PlayerInventoryNode(ParentNode parent, Attributes attributes) throws SAXException {
            super(parent);
            this.x = GuiParser.getIntAttr(attributes, "x");
            this.y = GuiParser.getIntAttr(attributes, "y");
        }

        @Override
        public NodeType getType() {
            return NodeType.playerinventory;
        }
    }

    public static class ImageNode
    extends Node {
        public final int x;
        public final int y;
        public final int width;
        public final int height;
        public final int u;
        public final int v;
        public final ResourceLocation src;

        ImageNode(ParentNode parent, Attributes attributes) throws SAXException {
            String file;
            String domain;
            super(parent);
            this.x = GuiParser.getIntAttr(attributes, "x");
            this.y = GuiParser.getIntAttr(attributes, "y");
            this.width = GuiParser.getIntAttr(attributes, "width", -1);
            this.height = GuiParser.getIntAttr(attributes, "height", -1);
            this.u = GuiParser.getIntAttr(attributes, "u", 0);
            this.v = GuiParser.getIntAttr(attributes, "v", 0);
            String resLoc = GuiParser.getAttr(attributes, "src");
            if (resLoc.isEmpty()) {
                throw new SAXException("empty src");
            }
            int pos = resLoc.indexOf(58);
            if (pos == -1) {
                domain = "ic2";
                file = resLoc;
            } else {
                domain = resLoc.substring(0, pos);
                file = resLoc.substring(pos + 1);
            }
            if (!file.endsWith(".png")) {
                file = file + ".png";
            }
            this.src = new ResourceLocation(domain, file);
        }

        @Override
        public NodeType getType() {
            return NodeType.image;
        }
    }

    public static class GaugeNode
    extends Node {
        public final int x;
        public final int y;
        public final String name;
        public final Gauge.GaugeStyle style;

        GaugeNode(ParentNode parent, Attributes attributes) throws SAXException {
            super(parent);
            this.x = GuiParser.getIntAttr(attributes, "x");
            this.y = GuiParser.getIntAttr(attributes, "y");
            this.name = GuiParser.getAttr(attributes, "name");
            String styleName = GuiParser.getAttr(attributes, "style", "normal");
            this.style = Gauge.GaugeStyle.get(styleName);
            if (this.style == null) {
                throw new SAXException("invalid gauge style: " + styleName);
            }
        }

        @Override
        public NodeType getType() {
            return NodeType.gauge;
        }
    }

    public static class EnergyGaugeNode
    extends Node {
        public final int x;
        public final int y;
        public final EnergyGauge.EnergyGaugeStyle style;

        EnergyGaugeNode(ParentNode parent, Attributes attributes) throws SAXException {
            super(parent);
            this.x = GuiParser.getIntAttr(attributes, "x");
            this.y = GuiParser.getIntAttr(attributes, "y");
            String styleName = GuiParser.getAttr(attributes, "style", "bolt");
            this.style = EnergyGauge.EnergyGaugeStyle.get(styleName);
            if (this.style == null) {
                throw new SAXException("invalid gauge style: " + styleName);
            }
        }

        @Override
        public NodeType getType() {
            return NodeType.energygauge;
        }
    }

    public static class EnvironmentNode
    extends ParentNode {
        public final GuiEnvironment environment;

        EnvironmentNode(ParentNode parent, Attributes attributes) throws SAXException {
            super(parent);
            String name = GuiParser.getAttr(attributes, "name");
            this.environment = GuiEnvironment.get(name);
            if (this.environment == null) {
                throw new SAXException("invalid environment name: " + name);
            }
        }

        @Override
        public NodeType getType() {
            return NodeType.environment;
        }
    }

    public static class GuiNode
    extends ParentNode {
        private final Class<?> baseClass;
        final int width;
        final int height;

        GuiNode(Attributes attributes, Class<?> baseClass) throws SAXException {
            super(null);
            this.baseClass = baseClass;
            this.width = GuiParser.getIntAttr(attributes, "width");
            this.height = GuiParser.getIntAttr(attributes, "height");
        }

        @Override
        public NodeType getType() {
            return NodeType.gui;
        }

        @Override
        public Class<?> getBaseClass() {
            return this.baseClass;
        }
    }

    public static abstract class ParentNode
    extends Node {
        final List<Node> children = new ArrayList<Node>();

        ParentNode(ParentNode parent) {
            super(parent);
        }

        public void addNode(Node node) {
            this.children.add(node);
        }

        public Iterable<Node> getNodes() {
            return this.children;
        }

        public Class<?> getBaseClass() {
            return this.parent.getBaseClass();
        }
    }

    public static abstract class Node {
        final ParentNode parent;

        Node(ParentNode parent) {
            this.parent = parent;
        }

        public abstract NodeType getType();

        public void setContent(String content) throws SAXException {
            throw new SAXException("unexpected characters");
        }
    }

    public static enum NodeType {
        gui,
        environment,
        energygauge,
        gauge,
        image,
        playerinventory,
        slot,
        slotgrid,
        text,
        fluidtank,
        fluidslot;
        
        private static Map<String, NodeType> map;

        private NodeType() {
        }

        public static NodeType get(String name) {
            return map.get(name);
        }

        private static Map<String, NodeType> getMap() {
            NodeType[] values = NodeType.values();
            HashMap<String, NodeType> ret = new HashMap<String, NodeType>(values.length);
            for (NodeType type : values) {
                ret.put(type.name(), type);
            }
            return ret;
        }

        static {
            map = NodeType.getMap();
        }
    }

    private static class SaxHandler
    extends DefaultHandler {
        private final Class<?> baseClass;
        private ParentNode parentNode;
        private Node currentNode;

        public SaxHandler(Class<?> baseClass) {
            this.baseClass = baseClass;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            NodeType type = NodeType.get(qName);
            if (type == null) {
                type = NodeType.get(qName.toLowerCase(Locale.ENGLISH));
            }
            if (type == null) {
                throw new SAXException("invalid element: " + qName);
            }
            if (type == NodeType.gui) {
                if (this.parentNode != null) {
                    throw new SAXException("invalid gui element location");
                }
            } else if (this.parentNode == null) {
                throw new SAXException("invalid " + qName + " element location");
            }
            switch (type) {
                case gui: {
                    this.currentNode = this.parentNode = new GuiNode(attributes, this.baseClass);
                    break;
                }
                case environment: {
                    this.currentNode = new EnvironmentNode(this.parentNode, attributes);
                    this.parentNode.addNode(this.currentNode);
                    this.parentNode = (ParentNode)this.currentNode;
                    break;
                }
                case energygauge: {
                    this.currentNode = new EnergyGaugeNode(this.parentNode, attributes);
                    this.parentNode.addNode(this.currentNode);
                    break;
                }
                case gauge: {
                    this.currentNode = new GaugeNode(this.parentNode, attributes);
                    this.parentNode.addNode(this.currentNode);
                    break;
                }
                case image: {
                    this.currentNode = new ImageNode(this.parentNode, attributes);
                    this.parentNode.addNode(this.currentNode);
                    break;
                }
                case playerinventory: {
                    this.currentNode = new PlayerInventoryNode(this.parentNode, attributes);
                    this.parentNode.addNode(this.currentNode);
                    break;
                }
                case slot: {
                    this.currentNode = new SlotNode(this.parentNode, attributes);
                    this.parentNode.addNode(this.currentNode);
                    break;
                }
                case slotgrid: {
                    this.currentNode = new SlotGridNode(this.parentNode, attributes);
                    this.parentNode.addNode(this.currentNode);
                    break;
                }
                case text: {
                    this.currentNode = new TextNode(this.parentNode, attributes);
                    this.parentNode.addNode(this.currentNode);
                    break;
                }
                case fluidtank: {
                    this.currentNode = new FluidTankNode(this.parentNode, attributes);
                    this.parentNode.addNode(this.currentNode);
                    break;
                }
                case fluidslot: {
                    this.currentNode = new FluidSlotNode(this.parentNode, attributes);
                    this.parentNode.addNode(this.currentNode);
                }
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            while (length > 0 && Character.isWhitespace(ch[start])) {
                ++start;
                --length;
            }
            while (length > 0 && Character.isWhitespace(ch[start + length - 1])) {
                --length;
            }
            if (length != 0) {
                if (this.currentNode == null) {
                    throw new SAXException("unexpected characters");
                }
                this.currentNode.setContent(new String(ch, start, length));
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            this.currentNode = this.currentNode == this.parentNode ? (this.currentNode.getType() == NodeType.gui ? null : (this.parentNode = this.parentNode.parent)) : this.parentNode;
        }

        public GuiNode getResult() {
            return (GuiNode)this.parentNode;
        }
    }

}

