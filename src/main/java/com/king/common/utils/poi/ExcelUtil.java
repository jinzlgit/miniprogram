package com.king.common.utils.poi;

import com.king.common.exception.BusinessException;
import com.king.common.utils.DateUtils;
import com.king.common.utils.StringUtils;
import com.king.common.utils.reflect.ReflectUtils;
import com.king.common.utils.text.Convert;
import com.king.framework.aspect.annotation.Excel;
import com.king.framework.aspect.annotation.Excels;
import com.king.framework.config.ProjectConfig;
import com.king.framework.web.domain.Result;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Excel相关操作
 *
 * @author 金振林
 * @version v1.0
 * @date 2020/12/22 10:01
 */
public class ExcelUtil<T> {

    private static final Logger log = LoggerFactory.getLogger(ExcelUtil.class);

    /**
     * Excel sheet最大行数，默认65535
     */
    public static final int sheetSize = 65535;

    /**
     * 工作表名称
     */
    private String sheetName;

    /**
     * 导出类型（EXPORT：导出数据；IMPORT：导入模板）
     */
    private Excel.Type type;

    /**
     * 工作簿对象
     */
    private Workbook wb;

    /**
     * 工作表对象
     */
    private Sheet sheet;

    /**
     * 导入导出数据列表
     */
    private List<T> list;

    /**
     * 注解列表
     */
    private List<Field> fields;

    /**
     * 实体对象
     */
    public Class<T> clazz;

    /**
     * 构造函数
     */
    public ExcelUtil(Class<T> clazz) {
        this.clazz = clazz;
    }

    public void init(List<T> list, String sheetName, Excel.Type type) {
        if (list == null) {
            list = new ArrayList<T>();
        }
        this.list = list;
        this.sheetName = sheetName;
        this.type = type;
        createExcelField();
        createWorkbook();
    }

    /**
     * 对Excel表单默认第一个索引名转换成list
     *
     * @param is 输入流
     * @return java.util.List<T>
     * @author 金振林
     * @date 2020/12/22 10:26
     */
    public List<T> importExcel(InputStream is) throws Exception {
        return importExcel(StringUtils.EMPTY, is);
    }

    /**
     * 对excel表单指定表格索引名转换成list
     *
     * @param sheetName 表格索引名
     * @param is 输入流
     * @return java.util.List<T>
     * @author 金振林
     * @date 2020/12/22 11:40
     */
    public List<T> importExcel(String sheetName, InputStream is) throws Exception {
        this.type = Excel.Type.IMPORT;
        this.wb = WorkbookFactory.create(is);
        List<T> list = new ArrayList<T>();
        Sheet sheet = null;
        if (StringUtils.isEmpty(sheetName)) {
            // 如果指定sheet名，则取指定sheet中的内容
            sheet = wb.getSheet(sheetName);
        } else {
            // 如果传入的sheet名不存在则默认指向第一个sheet
            sheet = wb.getSheetAt(0);
        }

        if (sheet == null) {
            throw new IOException("文件sheet不存在");
        }

        int rows = sheet.getPhysicalNumberOfRows();

        if (rows > 0) {
            // 定义一个map用于存放excel列的序号和field
            Map<String, Integer> cellMap = new HashMap<>();
            // 获取表头
            Row head = sheet.getRow(0);
            for (int i = 0; i < head.getPhysicalNumberOfCells(); i++) {
                Cell cell = head.getCell(i);
                if (StringUtils.isNotNull(cell)) {
                    String value = this.getCellValue(head, i).toString();
                    cellMap.put(value, i);
                } else {
                    cellMap.put(null, i);
                }
            }
            // 有数据时才处理，得到类的所有field
            /*
             * getFields()：获得某个类的所有的公共（public）的字段，包括父类中的字段。
             * getDeclaredFields()：获得某个类的所有声明的字段，即包括public、private和proteced，但是不包括父类的申明字段。
             * 同样类似的还有getConstructors()和getDeclaredConstructors()、getMethods()和getDeclaredMethods()，这两者分别表示获取某个类的方法、构造函数。
             */
            Field[] allFields = clazz.getDeclaredFields();
            // 定义一个map用于存放列的序号和field
            Map<Integer, Field> fieldMap = new HashMap<>();
            for (int col = 0; col < allFields.length; col++) {
                Field field = allFields[col];
                Excel attr = field.getAnnotation(Excel.class);
                if (attr != null && (attr.type() == Excel.Type.ALL || attr.type() == type)) {
                    // 设置类的私有字段属性可访问
                    field.setAccessible(true);
                    Integer column = cellMap.get(attr.name());
                    fieldMap.put(column, field);
                }
            }
            for (int i = 1; i < rows; i++) {
                // 从第二行开始读取数据，默认第一行是表头
                Row row = sheet.getRow(i);
                T entity = null;
                for (Map.Entry<Integer, Field> entry : fieldMap.entrySet()) {
                    Object val = this.getCellValue(row, entry.getKey());
                    // 如果对象实例不存在则新建
                    entity = (entity == null ? clazz.newInstance() : entity);
                    // 从map中得到对应列的field
                    Field field = fieldMap.get(entry.getKey());
                    // 取得类型，并根据对象类型设置值
                    Class<?> fieldType = field.getType();
                    if (String.class == fieldType) {
                        String s = Convert.toStr(val);
                        if (StringUtils.endsWith(s, ".0")) {
                            val = StringUtils.substringBefore(s, ".0");
                        } else {
                            val = Convert.toStr(val);
                        }
                    } else if ((Integer.TYPE == fieldType) || (Integer.class == fieldType)) {
                        val = Convert.toInt(val);
                    } else if ((Long.TYPE == fieldType) || (Long.class == fieldType)) {
                        val = Convert.toLong(val);
                    } else if ((Double.TYPE == fieldType) || (Double.class == fieldType)) {
                        val = Convert.toInt(val);
                    } else if ((Float.TYPE == fieldType) || (Float.class == fieldType)) {
                        val = Convert.toInt(val);
                    } else if (BigDecimal.class == fieldType) {
                        val = Convert.toBigDecimal(val);
                    } else if (Date.class == fieldType) {
                        if (val instanceof String) {
                            val = DateUtils.parseDate(val);
                        } else if (val instanceof Double) {
                            val = DateUtil.getJavaDate((Double) val);
                        }
                    }

                    if (StringUtils.isNotNull(fieldType)) {
                        Excel attr = field.getAnnotation(Excel.class);
                        String entityPropertyName = field.getName();
                        if (StringUtils.isNotEmpty(attr.targetAttr())) {
                            entityPropertyName = field.getName() + "." + attr.targetAttr();
                        } else if (StringUtils.isNotEmpty(attr.readConverterExp())) {
                            val = reverseByExp(String.valueOf(val), attr.readConverterExp());
                        }
                        ReflectUtils.invokeSetter(entity, entityPropertyName, val);
                    }
                }
                list.add(entity);
            }
        }
        return list;
    }

    /**
     * 对list数据将其里面的数据导出到Excel表单
     *
     * @param list 导出数据集合
     * @param sheetName 工作表名称
     * @return com.king.framework.web.domain.Result
     * @author 金振林
     * @date 2020/12/22 13:36
     */
    public Result exportExcel(List<T> list, String sheetName) {
        this.init(list, sheetName, Excel.Type.EXPORT);
        return exportExcel();
    }

    /**
     * 导入
     *
     * @param sheetName 工作表名称
     * @return com.king.framework.web.domain.Result
     * @author 金振林
     * @date 2020/12/22 13:38
     */
    public Result exportExcel(String sheetName) {
        this.init(null, sheetName, Excel.Type.IMPORT);
        return exportExcel();
    }

    /**
     * 导出到Excel
     *
     * @param
     * @return com.king.framework.web.domain.Result
     * @author 金振林
     * @date 2020/12/22 14:01
     */
    public Result exportExcel() {
        OutputStream out = null;
        try {
            // 取出一共有多少个sheet
            double sheetNo = Math.ceil(list.size() / sheetSize);
            for (int index = 0; index <= sheetNo; index++) {
                createSheet(sheetNo, index);

                // 产生一行，第一行即表头
                Row row = sheet.createRow(0);
                int excelsNo = 0;
                // 写入各个字段的列头名称
                for (int column = 0; column < fields.size(); column++) {
                    Field field = fields.get(column);
                    if (field.isAnnotationPresent(Excel.class)) {
                        Excel excel = field.getAnnotation(Excel.class);
                        createCell(excel, row, column);
                    }
                    if (field.isAnnotationPresent(Excels.class)) {
                        Excels attrs = field.getAnnotation(Excels.class);
                        Excel[] excels = attrs.value();
                        // 写入列名
                        Excel excel = excels[excelsNo++];
                        createCell(excel, row, column);
                    }
                }
                if (Excel.Type.EXPORT.equals(type)) {
                    fillExcelData(index, row);
                }
            }
            String filename = encodingFilename(sheetName);
            out = new FileOutputStream(getAbsolutFile(filename));
            wb.write(out);
            return Result.ok();
        } catch (Exception e) {
            log.error("导出Excel异常{}", e.getMessage());
            throw new BusinessException("导出Excel失败");
        } finally {
            if (wb != null) {
                try {
                    wb.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 填充Excel数据
     *
     * @param index 序号
     * @param row 单元格行
     * @return void
     * @author 金振林
     * @date 2020/12/22 14:13
     */
    public void fillExcelData(int index, Row row) {
        int startNo = index * sheetSize;
        int endNo = Math.min(startNo + sheetSize, list.size());
        // 写入各条记录，每条记录对应excel表中的一行
        CellStyle cs = wb.createCellStyle();
        cs.setAlignment(HorizontalAlignment.CENTER);
        cs.setVerticalAlignment(VerticalAlignment.CENTER);
        for (int i = startNo; i < endNo; i++) {
            row = sheet.createRow(i + 1 - startNo);
            // 得到导出对象
            T vo = (T) list.get(i);
            int excelsNo = 0;
            for (int column = 0; column < fields.size(); column++) {
                // 获得field
                Field field = fields.get(column);
                // 设置实体类私有属性可访问
                field.setAccessible(true);
                if (field.isAnnotationPresent(Excel.class)) {
                    addCell(field.getAnnotation(Excel.class), row, vo, field, column, cs);
                }
                if (field.isAnnotationPresent(Excels.class)) {
                    Excels attrs = field.getAnnotation(Excels.class);
                    Excel[] excels = attrs.value();
                    Excel excel = excels[excelsNo++];
                    addCell(excel, row, vo, field, column, cs);
                }
            }
        }
    }

    /**
     * 创建单元格
     *
     * @param attr
     * @param row
     * @param column
     * @return org.apache.poi.ss.usermodel.Cell
     * @author 金振林
     * @date 2020/12/22 14:16
     */
    public Cell createCell(Excel attr, Row row, int column) {
        // 创建列
        Cell cell = row.createCell(column);
        // 设置列中写入内容为String的类型
        cell.setCellType(CellType.STRING);
        // 写入列名
        cell.setCellValue(attr.name());
        CellStyle cellStyle = createCellStyle(attr, row, column);
        cell.setCellStyle(cellStyle);
        return cell;
    }

    /**
     * 创建表格样式
     *
     * @param attr
     * @param row
     * @param column
     * @return org.apache.poi.ss.usermodel.CellStyle
     * @author 金振林
     * @date 2020/12/22 14:30
     */
    public CellStyle createCellStyle(Excel attr, Row row, int column) {
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // 导出字段是否有 "注："
        if (attr.name().indexOf("注：") >= 0) {
            Font font = wb.createFont();
            font.setColor(HSSFFont.COLOR_RED);
            cellStyle.setFont(font);
            cellStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.YELLOW.getIndex());
            sheet.setColumnWidth(column, 6000);
        } else {
            Font font = wb.createFont();
            // 粗体显示
            font.setBold(true);
            // 选择需要用到的字体格式
            cellStyle.setFont(font);
            cellStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.LIGHT_YELLOW.getIndex());
            // 设置列宽
            sheet.setColumnWidth(column, (int) ((attr.width() + 0.72) * 256));
            row.setHeight((short) (attr.height() * 20));
        }
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setWrapText(true);
        // 如果设置了提示信息则鼠标放上去提示
        if (StringUtils.isNotEmpty(attr.prompt())) {
            // 这里默认设置了2-101列提示
            setXSSFPrompt(sheet, "", attr.prompt(), 1, 100, column, column);
        }
        // 如果设置了combo属性则本列只能选择不能输入
        if (attr.combo().length > 0) {
            // 这里默认设置了2-101列只能选择不能输入
            setXSSFValidation(sheet, attr.combo(), 1, 100, column, column);
        }
        return cellStyle;
    }

    /**
     * 添加单元格
     *
     * @param attr
     * @param row
     * @param vo
     * @param field
     * @param column
     * @param cs
     * @return org.apache.poi.ss.usermodel.Cell
     * @author 金振林
     * @date 2020/12/22 14:41
     */
    public Cell addCell(Excel attr, Row row, T vo, Field field, int column, CellStyle cs) {
        Cell cell = null;
        try {
            // 设置行高
            row.setHeight((short) (attr.height() * 20));
            // 根据Excel中设置情况决定是否导出，有些情况需要保持为空，希望用户填写这一列
            if (attr.isExport()) {
                // 创建cell
                cell = row.createCell(column);
                cell.setCellStyle(cs);

                // 用于读取对象中的属性
                Object value = getTargetValue(vo, field, attr);
                String dateFormat = attr.dateFormat();
                String readConverterExp = attr.readConverterExp();
                if (StringUtils.isNotEmpty(dateFormat) && StringUtils.isNotNull(value)) {
                    cell.setCellValue(DateUtils.parseDateToStr(dateFormat, (Date) value));
                } else if (StringUtils.isNotEmpty(readConverterExp) && StringUtils.isNotNull(value)) {
                    cell.setCellValue(convertByExp(String.valueOf(value), readConverterExp));
                } else {
                    cell.setCellType(CellType.STRING);
                    // 如果数据存在就填入，不存在填入空格
                    cell.setCellValue(StringUtils.isNull(value) ? attr.defaultValue() : value + attr.suffix());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("导出Excel失败{}", e);
        }
        return cell;
    }

    /**
     * 设置POI XSSFSheet 单元格提示
     *
     * @param sheet 表单
     * @param promptTitle 提示标题
     * @param promptContent 提示内容
     * @param firstRow 开始行
     * @param endRow 结束行
     * @param firstCol 开始列
     * @param endCol 结束列
     * @return void
     * @author 金振林
     * @date 2020/12/22 14:58
     */
    public void setXSSFPrompt(Sheet sheet, String promptTitle, String promptContent, int firstRow, int endRow, int firstCol, int endCol) {
        DataValidationHelper helper = sheet.getDataValidationHelper();
        DataValidationConstraint constraint = helper.createCustomConstraint("DD1");
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, endRow, firstCol, endCol);
        DataValidation dataValidation = helper.createValidation(constraint, regions);
        dataValidation.createPromptBox(promptTitle, promptContent);
        dataValidation.setShowPromptBox(true);
        sheet.addValidationData(dataValidation);
    }

    /**
     * 设置某些列的值只能输入预设的数据，显示下拉框
     *
     * @param sheet
     * @param textList
     * @param firstRow
     * @param endRow
     * @param firstVol
     * @param endCol
     * @return void
     * @author 金振林
     * @date 2020/12/22 15:07
     */
    public void setXSSFValidation(Sheet sheet, String[] textList, int firstRow, int endRow, int firstVol, int endCol) {
        DataValidationHelper helper = sheet.getDataValidationHelper();
        // 加载下拉列表内容
        DataValidationConstraint constraint = helper.createExplicitListConstraint(textList);
        // 设置数据有效性加载在哪个单元格上
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, endRow, firstVol, endCol);
        // 数据有效性对象
        DataValidation dataValidation = helper.createValidation(constraint, regions);
        // 处理Excel兼容性问题
        if (dataValidation instanceof XSSFDataValidation) {
            dataValidation.setSuppressDropDownArrow(true);
            dataValidation.setShowErrorBox(true);
        } else {
            dataValidation.setSuppressDropDownArrow(false);
        }
        sheet.addValidationData(dataValidation);
    }

    /**
     * 解析导出值 0=男,1=女
     *
     * @param propertyValue 参数值
     * @param converterExp 翻译注解
     * @return java.lang.String
     * @author 金振林
     * @date 2020/12/22 16:41
     */
    public static String convertByExp(String propertyValue, String converterExp) {
        try {
            String[] convertSource = converterExp.split(",");
            for (String item : convertSource) {
                String[] itemArray = item.split("=");
                if (itemArray[0].equals(propertyValue)) {
                    return itemArray[1];
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return propertyValue;
    }

    /**
     * 反向解析值 男=0,女=1
     *
     * @param propertyValue 参数值
     * @param converterExp 翻译注解
     * @return java.lang.String
     * @author 金振林
     * @date 2020/12/22 11:33
     */
    public static String reverseByExp(String propertyValue, String converterExp) throws Exception {
        try {
            String[] convertSource = converterExp.split(",");
            for (String item : convertSource) {
                String[] itemArray = item.split("=");
                if (itemArray[1].equals(propertyValue)) {
                    return itemArray[0];
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return propertyValue;
    }

    /**
     * 编码文件名
     *
     * @param filename 原文件名
     * @return java.lang.String
     * @author 金振林
     * @date 2020/12/22 16:45
     */
    public String encodingFilename(String filename) {
        filename = UUID.randomUUID().toString() + "_" + filename + ".xlsx";
        return filename;
    }

    /**
     * 获取下载路径
     *
     * @param filename 文件名称
     * @return java.lang.String
     * @author 金振林
     * @date 2020/12/22 17:01
     */
    public String getAbsolutFile(String filename) {
        String downloadPath = ProjectConfig.getDownloadPath() + filename;
        File desc = new File(downloadPath);
        if (!desc.getParentFile().exists()) {
            desc.getParentFile().mkdirs();
        }
        return downloadPath;
    }

    /**
     * 获取Bean中的属性值
     *
     * @param vo 实体对象
     * @param field 字段
     * @param excel 注解
     * @return java.lang.Object
     * @author 金振林
     * @date 2020/12/22 17:03
     */
    private Object getTargetValue(T vo, Field field, Excel excel) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Object o = field.get(vo);
        if (StringUtils.isNotEmpty(excel.targetAttr())) {
            String target = excel.targetAttr();
            if (target.indexOf(".") > -1) {
                String[] targets = target.split(".");
                for (String name : targets) {
                    o = getValue(o, name);
                }
            } else {
                o = getValue(o, target);
            }
        }
        return o;
    }

    /**
     * 以类的属性的get方法形式获取值
     *
     * @param o 对象
     * @param name 字段
     * @return java.lang.Object
     * @author 金振林
     * @date 2020/12/22 17:08
     */
    private Object getValue(Object o, String name) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        if (StringUtils.isNotEmpty(name)) {
            Class<?> clazz = o.getClass();
            String methodName = "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
            Method method = clazz.getMethod(methodName);
            o = method.invoke(o);
        }
        return o;
    }

    /**
     * 得到所有含有注解Excel定义的字段
     *
     * @param
     * @return void
     * @author 金振林
     * @date 2020/12/22 17:15
     */
    private void createExcelField() {
        this.fields = new ArrayList<>();
        List<Field> tempFields = new ArrayList<>();
        tempFields.addAll(Arrays.asList(clazz.getSuperclass().getDeclaredFields()));
        tempFields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        for (Field field : tempFields) {
            // 单注解
            if (field.isAnnotationPresent(Excel.class)) {
                putToField(field, field.getAnnotation(Excel.class));
            }
            // 多注解
            if (field.isAnnotationPresent(Excels.class)) {
                Excels attrs = field.getAnnotation(Excels.class);
                Excel[] excels = attrs.value();
                for (Excel excel : excels) {
                    putToField(field, excel);
                }
            }
        }
    }

    /**
     * 放到字段集合中
     */
    private void putToField(Field field, Excel attr) {
        if (attr != null && (attr.type() == Excel.Type.ALL || attr.type() == type)) {
            this.fields.add(field);
        }
    }

    /**
     * 创建一个工作簿
     */
    public void createWorkbook() {
        this.wb = new SXSSFWorkbook(500);
    }

    /**
     * 创建工作表
     *
     * @param sheetNo sheet数量
     * @param index 序号
     * @return void
     * @author 金振林
     * @date 2020/12/22 17:20
     */
    public void createSheet(double sheetNo, int index) {
        this.sheet = wb.createSheet();
        // 设置工作表名称
        if (sheetNo == 0) {
            wb.setSheetName(index, sheetName);
        } else {
            wb.setSheetName(index, sheetName + index);
        }
    }

    /**
     * 获取单元格值
     *
     * @param row 获取的行
     * @param colum 获取单元格列号
     * @return java.lang.Object
     * @author 金振林
     * @date 2020/12/22 10:38
     */
    public Object getCellValue(Row row, int colum) {
        if (row == null) {
            return row;
        }
        Object val = "";
        try {
            Cell cell = row.getCell(colum);
            if (cell != null) {
                if (cell.getCellTypeEnum() == CellType.NUMERIC) {
                    val = cell.getNumericCellValue();
                    if (HSSFDateUtil.isCellDateFormatted(cell)) {
                        // POI Excel 日期格式转换
                        val = DateUtil.getJavaDate((Double) val);
                    } else {
                        if ((Double) val % 1 > 0) {
                            val = new DecimalFormat("0.00").format(val);
                        } else {
                            val = new DecimalFormat("0").format(val);
                        }
                    }
                } else if (cell.getCellTypeEnum() == CellType.STRING) {
                    val = cell.getStringCellValue();
                } else if (cell.getCellTypeEnum() == CellType.BOOLEAN) {
                    val = cell.getBooleanCellValue();
                } else if (cell.getCellTypeEnum() == CellType.ERROR) {
                    val = cell.getErrorCellValue();
                }
            }
        } catch (Exception e) {
            return val;
        }
        return val;
    }

}
