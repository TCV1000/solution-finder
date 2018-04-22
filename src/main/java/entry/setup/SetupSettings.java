package entry.setup;

import common.datastore.BlockField;
import common.datastore.Operation;
import common.datastore.SimpleOperation;
import common.parser.StringEnumTransform;
import common.tetfu.common.ColorConverter;
import common.tetfu.common.ColorType;
import common.tetfu.field.ColoredField;
import core.field.Field;
import core.mino.Piece;
import core.srs.Rotate;
import entry.DropType;
import exceptions.FinderParseException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SetupSettings {
    private static final String DEFAULT_LOG_FILE_PATH = "output/last_output.txt";
    private static final String DEFAULT_OUTPUT_BASE_FILE_PATH = "output/setup.html";

    private String logFilePath = DEFAULT_LOG_FILE_PATH;
    private boolean isReserved = false;
    private boolean isUsingHold = true;
    private boolean isCombination = false;
    private ExcludeType exclude = ExcludeType.None;
    private List<Operation> addOperations = Collections.emptyList();
    private List<Integer> assumeFilledLines = Collections.emptyList();
    private int maxHeight = -1;
    private List<String> patterns = new ArrayList<>();
    private Field initField = null;
    private Field needFilledField = null;
    private Field notFilledField = null;
    private BlockField reservedBlock = null;
    private ColorType marginColorType = null;
    private ColorType fillColorType = null;
    private DropType dropType = DropType.Softdrop;
    private String outputBaseFilePath = DEFAULT_OUTPUT_BASE_FILE_PATH;

    // ********* Getter ************
    public boolean isUsingHold() {
        return isUsingHold;
    }

    String getLogFilePath() {
        return logFilePath;
    }

    String getOutputBaseFilePath() {
        return outputBaseFilePath;
    }

    boolean isReserved() {
        return isReserved;
    }

    ExcludeType getExcludeType() {
        return exclude;
    }

    boolean isCombination() {
        return isCombination;
    }

    List<Operation> getAddOperations() {
        return addOperations;
    }

    List<Integer> getAssumeFilledLines() {
        return assumeFilledLines;
    }

    int getMaxHeight() {
        return maxHeight;
    }

    List<String> getPatterns() {
        return patterns;
    }

    boolean isOutputToConsole() {
        return true;
    }

    Field getInitField() {
        return initField;
    }

    Field getNeedFilledField() {
        return needFilledField;
    }

    Field getNotFilledField() {
        return notFilledField;
    }

    BlockField getReservedBlock() {
        return reservedBlock;
    }

    ColorType getMarginColorType() {
        return marginColorType;
    }

    ColorType getFillColorType() {
        return fillColorType;
    }

    DropType getDropType() {
        return dropType;
    }

    // ********* Setter ************
    void setUsingHold(Boolean isUsingHold) {
        this.isUsingHold = isUsingHold;
    }

    void setLogFilePath(String path) {
        this.logFilePath = path;
    }

    void setOutputBaseFilePath(String path) {
        this.outputBaseFilePath = path;
    }

    void setReserved(boolean isReserved) {
        this.isReserved = isReserved;
    }

    void setCombination(boolean isCombination) {
        this.isCombination = isCombination;
    }

    private void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    void setPatterns(List<String> patterns) {
        this.patterns = patterns;
    }

    void setFieldWithReserved(Field initField, Field needFilledField, Field notFilledField, ColoredField coloredField, int maxHeight) {
        BlockField blockField = new BlockField(maxHeight);
        for (int y = 0; y < maxHeight; y++) {
            for (int x = 0; x < 10; x++) {
                ColorConverter colorConverter = new ColorConverter();
                ColorType colorType = colorConverter.parseToColorType(coloredField.getBlockNumber(x, y));
                switch (colorType) {
                    case Gray:
                    case Empty:
                        break;
                    default:
                        Piece piece = colorConverter.parseToBlock(colorType);
                        blockField.setBlock(piece, x, y);
                        break;
                }
            }
        }

        setMaxHeight(maxHeight);
        setInitField(initField);
        setNeedFilledField(needFilledField);
        setNotFilledField(notFilledField);
        setReservedBlock(blockField);
    }

    void setField(Field initField, Field needFilledField, Field notFilledField, int maxHeight) {
        setMaxHeight(maxHeight);
        setInitField(initField);
        setNeedFilledField(needFilledField);
        setNotFilledField(notFilledField);
        setReservedBlock(null);
    }

    private void setInitField(Field field) {
        this.initField = field;
    }

    private void setNeedFilledField(Field field) {
        this.needFilledField = field;
    }

    private void setNotFilledField(Field field) {
        this.notFilledField = field;
    }

    private void setReservedBlock(BlockField reservedBlock) {
        this.reservedBlock = reservedBlock;
    }

    void setMarginColorType(String marginColor) throws FinderParseException {
        try {
            this.marginColorType = parseToColor(marginColor);
        } catch (IllegalArgumentException e) {
            throw new FinderParseException("Unsupported margin color: value=" + marginColor);
        }
    }

    // The Tetris Company standardization
    private ColorType parseToColor(String color) {
        switch (color.trim().toLowerCase()) {
            case "i":
            case "cyan":
            case "cy":
                return ColorType.I;
            case "j":
            case "blue":
            case "bl":
                return ColorType.J;
            case "l":
            case "orange":
            case "or":
                return ColorType.L;
            case "o":
            case "yellow":
            case "ye":
                return ColorType.O;
            case "s":
            case "green":
            case "gr":
                return ColorType.S;
            case "t":
            case "purple":
            case "pu":
                return ColorType.T;
            case "z":
            case "red":
            case "re":
                return ColorType.Z;
            case "x":
            case "g":
            case "gray":
                return null;
            case "none":
            case "null":
            case "empty":
                return null;
            default:
                throw new IllegalArgumentException();
        }
    }

    void setFillColorType(String fillColor) throws FinderParseException {
        try {
            this.fillColorType = parseToColor(fillColor);
        } catch (IllegalArgumentException e) {
            throw new FinderParseException("Unsupported fill color: value=" + fillColor);
        }
    }

    void setDropType(String type) throws FinderParseException {
        switch (type.trim().toLowerCase()) {
            case "soft":
            case "softdrop":
                this.dropType = DropType.Softdrop;
                return;
            case "hard":
            case "harddrop":
                this.dropType = DropType.Harddrop;
                return;
            default:
                throw new FinderParseException("Unsupported droptype: type=" + type);
        }
    }

    void setExcludeType(String type) throws FinderParseException {
        switch (type.trim().toLowerCase()) {
            case "hole":
            case "holes":
                this.exclude = ExcludeType.Holes;
                return;
            case "none":
            case "":
                this.exclude = ExcludeType.None;
                return;
            default:
                throw new FinderParseException("Unsupported droptype: type=" + type);
        }
    }

    void setAddOperations(List<String> values) throws FinderParseException {
        ArrayList<Operation> operations = new ArrayList<>();

        Pattern pattern = Pattern.compile("(.*?)\\((\\d),(\\d)\\)");

        for (String value : values) {
            value = value.trim().replace(" ", "");

            Matcher matcher = pattern.matcher(value);
            if (!matcher.find()) {
                throw new FinderParseException("Unsupported piece: add-piece=" + value);
            }

            String pieceName = matcher.group(1);

            Piece piece = StringEnumTransform.toPiece(pieceName.toUpperCase().charAt(0));
            Rotate rotate = Rotate.Spawn;
            if (pieceName.contains("-")) {
                rotate = toRotate(pieceName.split("-")[1].toLowerCase());
            }

            int x = Integer.valueOf(matcher.group(2));
            int y = Integer.valueOf(matcher.group(3));
            operations.add(new SimpleOperation(piece, rotate, x, y));
        }

        this.addOperations = operations;
    }

    private Rotate toRotate(String name) {
        switch (name) {
            case "0":
            case "spawn":
                return Rotate.Spawn;
            case "l":
            case "left":
                return Rotate.Left;
            case "2":
            case "reverse":
                return Rotate.Reverse;
            case "r":
            case "right":
                return Rotate.Right;
        }
        throw new IllegalArgumentException("No reachable");
    }
}
