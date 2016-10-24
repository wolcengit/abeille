/**
 * 
 */
package com.jeta.forms.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Wolcen
 *
 */
public class ObjectConvert {
	
	public static boolean isClassEquals(Class clazz1,Class clazz2) {
		if(clazz1.equals(clazz2)) return true;
		
		if(Boolean.class.equals(clazz1) && (boolean.class.equals(clazz2)) ) {
			return true;
		}else if(boolean.class.equals(clazz1) && (Boolean.class.equals(clazz2)) ) {
			return true;
		}else if(Byte.class.equals(clazz1) && (byte.class.equals(clazz2)) ) {
			return true;
		}else if(byte.class.equals(clazz1) && (Byte.class.equals(clazz2)) ) {
			return true;
		}else if(Character.class.equals(clazz1) && (char.class.equals(clazz2)) ) {
			return true;
		}else if(char.class.equals(clazz1) && (Character.class.equals(clazz2)) ) {
			return true;
		}else if(Short.class.equals(clazz1) && (short.class.equals(clazz2)) ) {
			return true;
		}else if(short.class.equals(clazz1) && (Short.class.equals(clazz2)) ) {
			return true;
		}else if(Integer.class.equals(clazz1) && (int.class.equals(clazz2)) ) {
			return true;
		}else if(int.class.equals(clazz1) && (Integer.class.equals(clazz2)) ) {
			return true;
		}else if(Long.class.equals(clazz1) && (long.class.equals(clazz2)) ) {
			return true;
		}else if(long.class.equals(clazz1) && (Long.class.equals(clazz2)) ) {
			return true;
		}else if(Float.class.equals(clazz1) && (float.class.equals(clazz2)) ) {
			return true;
		}else if(float.class.equals(clazz1) && (Float.class.equals(clazz2)) ) {
			return true;
		}else if(Double.class.equals(clazz1) && (double.class.equals(clazz2)) ) {
			return true;
		}else if(double.class.equals(clazz1) && (Double.class.equals(clazz2)) ) {
			return true;
		}else if(Boolean[].class.equals(clazz1) && (boolean[].class.equals(clazz2)) ) {
			return true;
		}else if(boolean[].class.equals(clazz1) && (Boolean[].class.equals(clazz2)) ) {
			return true;
		}else if(Byte[].class.equals(clazz1) && (byte[].class.equals(clazz2)) ) {
			return true;
		}else if(byte[].class.equals(clazz1) && (Byte[].class.equals(clazz2)) ) {
			return true;
		}else if(Character[].class.equals(clazz1) && (char[].class.equals(clazz2)) ) {
			return true;
		}else if(char[].class.equals(clazz1) && (Character[].class.equals(clazz2)) ) {
			return true;
		}else if(Short[].class.equals(clazz1) && (short[].class.equals(clazz2)) ) {
			return true;
		}else if(short[].class.equals(clazz1) && (Short[].class.equals(clazz2)) ) {
			return true;
		}else if(Integer[].class.equals(clazz1) && (int[].class.equals(clazz2)) ) {
			return true;
		}else if(int[].class.equals(clazz1) && (Integer[].class.equals(clazz2)) ) {
			return true;
		}else if(Long[].class.equals(clazz1) && (long[].class.equals(clazz2)) ) {
			return true;
		}else if(long[].class.equals(clazz1) && (Long[].class.equals(clazz2)) ) {
			return true;
		}else if(Float[].class.equals(clazz1) && (float[].class.equals(clazz2)) ) {
			return true;
		}else if(float[].class.equals(clazz1) && (Float[].class.equals(clazz2)) ) {
			return true;
		}else if(Double[].class.equals(clazz1) && (double[].class.equals(clazz2)) ) {
			return true;
		}else if(double[].class.equals(clazz1) && (Double[].class.equals(clazz2)) ) {
			return true;
		}else{
			return false;
		}
	}
	
	public static Object Converter(Class targetType,Object value) {
		if(value == null) return null;
		Class sourceType = value.getClass();
		if(sourceType.equals(targetType)){
			return value;
		}
		
		if(sourceType == String.class && targetType == Integer.class ){
			return StringToInteger((String)value);
		}else if(sourceType == String.class && targetType == Short.class ){
			return StringToShort((String)value);
		}else if(sourceType == String.class && targetType == Long.class ){
			return StringToLong((String)value);
		}else if(sourceType == String.class && targetType == Float.class ){
			return StringToFloat((String)value);
		}else if(sourceType == String.class && targetType == Double.class ){
			return Double.parseDouble((String)value);
		}else if(sourceType == String.class && targetType == Byte.class ){
			return StringToByte((String)value);
		}else if(sourceType == String.class && targetType == Boolean.class ){
			return StringToBoolean((String)value);
		}else if(sourceType == String.class && targetType == BigDecimal.class ){
			return StringToBigDecimal((String)value);
			
		}else if(sourceType == String.class && targetType == int.class ){
			Integer rslt = StringToInteger((String)value);
			return rslt == null?rslt:rslt.intValue();
		}else if(sourceType == String.class && targetType == short.class ){
			Short rslt = StringToShort((String)value);
			return rslt == null?rslt:rslt.shortValue();
		}else if(sourceType == String.class && targetType == long.class ){
			Long rslt = StringToLong((String)value);
			return rslt == null?rslt:rslt.longValue();
		}else if(sourceType == String.class && targetType == float.class ){
			Float rslt = StringToFloat((String)value);
			return rslt == null?rslt:rslt.floatValue();
		}else if(sourceType == String.class && targetType == double.class ){
			Double rslt = StringToDouble((String)value);
			return rslt == null?rslt:rslt.doubleValue();
		}else if(sourceType == String.class && targetType == byte.class ){
			Byte rslt = StringToByte((String)value);
			return rslt == null?rslt:rslt.byteValue();
		}else if(sourceType == String.class && targetType == boolean.class ){
			Boolean rslt = StringToBoolean((String)value);
			return rslt == null?rslt:rslt.booleanValue();
			
		}else if(sourceType == String.class && targetType == Color.class ){
			return StringToColor((String)value);
		}else if(sourceType == String.class && targetType == Point.class ){
			return StringToPoint((String)value);
		}else if(sourceType == String.class && targetType == Dimension.class ){
			return StringToDimension((String)value);
		}else if(sourceType == String.class && targetType == Rectangle.class ){
			return StringToRectangle((String)value);
		}else if(sourceType == String.class && targetType == Font.class ){
			return StringToFont((String)value);
			
			
		}else if(sourceType == java.util.Date.class && targetType == java.sql.Date.class ){
			return new java.sql.Date(((Date)value).getTime());
		}else if(sourceType == java.util.Date.class && targetType == java.sql.Time.class ){
			return new java.sql.Time(((Date)value).getTime());
		}else if(sourceType == java.util.Date.class && targetType == java.sql.Timestamp.class ){
			return new java.sql.Timestamp(((Date)value).getTime());
			
		}else if(sourceType == java.sql.Date.class && targetType == java.util.Date.class ){
			return new java.util.Date(((java.sql.Date)value).getTime());
		}else if(sourceType == java.sql.Time.class && targetType == java.util.Date.class ){
			return new java.util.Date(((java.sql.Time)value).getTime());
		}else if(sourceType == java.sql.Timestamp.class && targetType == java.util.Date.class ){
			return new java.util.Date(((java.sql.Timestamp)value).getTime());
			
		}else if(targetType == String.class ){
			return value.toString();
		}
		return value;
	}

	
	public static BigDecimal StringToBigDecimal(String value) {
		if(value == null || value.isEmpty()){
			return null;
		}
		BigDecimal rslt = null;
		try {
			rslt = BigDecimal.valueOf(StringToDouble(value));
		} catch (NumberFormatException e) {
		}
		return rslt;
	}
	
	public static Double StringToDouble(String value) {
		if(value == null || value.isEmpty()){
			return null;
		}
		Double rslt = null;
		try {
			rslt = Double.valueOf(value);
		} catch (NumberFormatException e) {
		}
		return rslt;
	}
	
	public static Float StringToFloat(String value) {
		if(value == null || value.isEmpty()){
			return null;
		}
		Float rslt = null;
		try {
			rslt = Float.valueOf(value);
		} catch (NumberFormatException e) {
		}
		return rslt;
	}

	public static Long StringToLong(String value) {
		if(value == null || value.isEmpty()){
			return null;
		}
		Long rslt = null;
		try {
			rslt = Long.valueOf(value);
		} catch (NumberFormatException e) {
		}
		return rslt;
	}

	public static Integer StringToInteger(String value) {
		if(value == null || value.isEmpty()){
			return null;
		}
		Integer rslt = null;
		try {
			rslt = Integer.valueOf(value);
		} catch (NumberFormatException e) {
		}
		return rslt;
	}
	public static Short StringToShort(String value) {
		if(value == null || value.isEmpty()){
			return null;
		}
		Short rslt = null;
		try {
			rslt = Short.valueOf(value);
		} catch (NumberFormatException e) {
		}
		return rslt;
	}
	public static Byte StringToByte(String value) {
		if(value == null || value.isEmpty()){
			return null;
		}
		Byte rslt = null;
		try {
			rslt = Byte.valueOf(value);
		} catch (NumberFormatException e) {
		}
		return rslt;
	}
	
	public static Boolean StringToBoolean(String value) {
		if(value == null || value.isEmpty()){
			return false;
		}
		Boolean rslt = Boolean.valueOf(value);
		return rslt;
	}
	
    public static Color StringToColor(String value)
    {
		if(value == null || value.isEmpty()){
			return null;
		}
        String color = ((String) value).trim();

        int[] components = new int[3];

        try
        {
        	if(color.indexOf(",")>0){
        		// parse the components
        		String[] sa = color.split(",");
                for (int i = 0; i < components.length; i++)
                {
                	if(i < sa.length)
                		components[i] = Integer.parseInt(sa[i]);
                	else
                		components[i] = 0;
                }
	            // parse the transparency
	            if (sa.length >= 4)
	            {
	            	int alpha = Integer.parseInt(sa[3]);
	                return new Color(components[0], components[1], components[2], alpha);
	            }
	            else
	            {
	                return new Color(components[0], components[1], components[2]);
	            }
        	}else{
                // check the size of the string
               int minlength = components.length * 2;
               if (color.length() < minlength)
                {
                    return null;
                }

               // remove the leading #
                if (color.startsWith("#"))
                {
                    color = color.substring(1);
                }

        		
        		// parse the components
	            for (int i = 0; i < components.length; i++)
	            {
	                components[i] = Integer.parseInt(color.substring(2 * i, 2 * i + 2), 16);
	            }
	            // parse the transparency
	            if (color.length() >= minlength + 2)
	            {
	            	int alpha = Integer.parseInt(color.substring(minlength, minlength + 2), 16);
		            return new Color(components[0], components[1], components[2], alpha);
	            }
	            else
	            {
		            return new Color(components[0], components[1], components[2]);
	            }
        	}
        }
        catch (Exception e)
        {
        	return null;
        }
    }
	public static Point StringToPoint(String value){
		if(value == null) return null;
		String[] bs = value.split(",");
		if(bs.length != 2) return null;
		Point point = new Point(
				StringToInteger(bs[0]), 
				StringToInteger(bs[1])); 
		return point;
	}
	public static Dimension StringToDimension(String value){
		if(value == null) return null;
		String[] bs = value.split(",");
		if(bs.length != 2) return null;
		Dimension size = new Dimension(
				StringToInteger(bs[0]), 
				StringToInteger(bs[1])); 
		return size;
	}
	public static Rectangle StringToRectangle(String value){
		if(value == null) return null;
		String[] bs = value.split(",");
		if(bs.length != 4) return null;
		Rectangle rect = new Rectangle(
				StringToInteger(bs[0]), 
				StringToInteger(bs[1]), 
				StringToInteger(bs[2]), 
				StringToInteger(bs[3])); 
		return rect;
	}
	public static Font StringToFont(String value){
		if(value == null) return null;
		String[] bs = value.split(",");
		if(bs.length != 3) return null;
		Font font = new Font(bs[0], 
				StringToInteger(bs[1]), 
				StringToInteger(bs[2])); 
		return font;
	}
    
	public static String ColorToString(Color value){
		return value.getRed()+","+value.getGreen()+","+value.getBlue()+","+value.getAlpha();
	}
	public static String PointToString(Point value){
		return value.getX()+","+value.getY();
	}
	public static String DimensionToString(Dimension value){
		return value.width+","+value.height;
	}
	public static String RectangleToString(Rectangle value){
		return value.x+","+value.y+","+value.width+","+value.height;
	}
	public static String FontToString(Font value){
		return value.getFamily()+","+value.getStyle()+","+value.getSize();
	}
	
	
}
