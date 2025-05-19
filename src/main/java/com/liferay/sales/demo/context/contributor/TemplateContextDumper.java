package com.liferay.sales.demo.context.contributor;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.template.TemplateContextContributor;
import com.liferay.portal.kernel.util.StringBundler;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;

/**
 * A simple POC class that outputs all available template elements.
 * Can be used within a fragment.
 * 
 * @author olaf
 */
@Component(
	immediate = true,
	property = {"type=" + TemplateContextContributor.TYPE_GLOBAL},
	service = TemplateContextContributor.class
)
public class TemplateContextDumper
	implements TemplateContextContributor {

	@Override
	public void prepare(
		Map<String, Object> contextObjects, HttpServletRequest request) {
		
		contextObjects.put("templateDumper", new TemplateDumper(contextObjects));
	}

	public class TemplateDumper {
		private Map<String, Object> contextObjects;

		public TemplateDumper(Map<String, Object> contextObjects) {
			this.contextObjects = contextObjects;
		}
		
		@Override
		public String toString() {
			StringBuffer result = new StringBuffer("<ul>");
			Set<String> unsortedKeys = contextObjects.keySet();
			ArrayList<String> keys = new ArrayList<>();
			keys.addAll(unsortedKeys);
			Collections.sort(keys);
			for (String key : keys) {
				result.append("<li><b>")
				      .append(key)
					  .append("</b> ")
					  .append(visualization(key))
					  .append("</li>");
			}
			result.append("</ul>");
			return result.toString();
		}

		private String visualization(String key) {
			Object object = contextObjects.get(key);
			if(object != null) {
				try {
					if(key.equals("Request")) {
						Method keysMethod = ReflectionUtil.getDeclaredMethod(object.getClass(), "keys");
						Method valuesMethod = ReflectionUtil.getDeclaredMethod(object.getClass(), "values");
						Object keys = keysMethod.invoke(object);
						Object values = valuesMethod.invoke(object);
						return object.getClass().getName() + enumerateKeys(keys, values); 
					}
				}
				catch (Exception e) {
					e.printStackTrace();
					return e.getClass().getName() + " " + e.getMessage();
				}
				return toString(object);
			}
			return "<i>null</i>";
		}

		private String enumerateKeys(Object keys, Object values) throws Exception {
			StringBuffer result = new StringBuffer("<ul>");
			Method keyIteratorMethod = ReflectionUtil.getDeclaredMethod(keys.getClass(), "iterator");
			Method valueIteratorMethod = ReflectionUtil.getDeclaredMethod(values.getClass(), "iterator");
			Object keyIterator = keyIteratorMethod.invoke(keys);
			Object valueIterator = valueIteratorMethod.invoke(values);
			Method hasNextMethod = ReflectionUtil.getDeclaredMethod(keyIterator.getClass(), "hasNext");
			Method nextKeyMethod = ReflectionUtil.getDeclaredMethod(keyIterator.getClass(), "next");
			Method nextValueMethod = ReflectionUtil.getDeclaredMethod(valueIterator.getClass(), "next");
			while((boolean) hasNextMethod.invoke(keyIterator)) {
				Object key = nextKeyMethod.invoke(keyIterator);
				Object value = nextValueMethod.invoke(valueIterator);
				
				result.append("<li><strong>" + key.toString() + "</strong> " + toString(value) + "</li>");

				if(key.toString().equals("COLLECTION_STYLED_LAYOUT_STRUCTURE_ITEM_IDS")) {
					Method sizeMethod = ReflectionUtil.getDeclaredMethod(value.getClass(), "size");
					Method getMethod = ReflectionUtil.getDeclaredMethod(value.getClass(), "get", int.class);
					int size = (int) sizeMethod.invoke(value);
					result.append("<ul>");
					for(int i = 0; i < size; i++) {
						Object element = getMethod.invoke(value, i);
						result.append("<li>").append(element.getClass().getName());
						Method getAsStringMethod = ReflectionUtil.getDeclaredMethod(element.getClass(), "getAsString");
						Object stringRepresentation = getAsStringMethod.invoke(element);
						result.append(": ")
							.append(stringRepresentation.toString())
							.append("</li>");
					}
					result.append("</ul>");
				} else if(key.toString().equals("INFO_ITEM_REFERENCE")) {
					result.append("<ul>");
					result.append("<li>").append(value.getClass().getName()).append("</li>");
					try {
						Method getWrappedObjectMethod = ReflectionUtil.getDeclaredMethod(value.getClass(), "getWrappedObject");
						result.append("<li>1</li>");
						getWrappedObjectMethod.setAccessible(true);
						result.append("<li>2</li>");
						Object wrappedObject = getWrappedObjectMethod.invoke(value);
						result.append("<li>").append(wrappedObject.getClass().getName()).append("</li>");
					} catch(NoSuchMethodException e) {
						result.append("<li>")
							.append(e.getClass().getName())
							.append(" ")
							.append(e.getMessage())
							.append("</li>");
					}
					result.append("<ul>");
					for (Class<? extends Object> c = value.getClass(); c != null; c = c.getSuperclass()) {
						result.append("<li><strong>").append(c.getName()).append(":</strong></li>");
						for (Method method : c.getDeclaredMethods()) {
					//	  if (method.getAnnotation(PostConstruct.class) != null) {
						    result.append("<li>");
						    result.append(method.getName() + " " + Modifier.toString(method.getModifiers()) + " " + method.getParameterCount());
						    result.append("</li>");
					//	  }
						}
					}
					result.append("</ul>");
					result.append("</ul>");
				}
			}
			result.append("</ul>");
			return result.toString();
		}
		
		private String toString(Object object) {
			StringBundler result = new StringBundler(object.getClass().getName());
			if(hasSize(object)) {
				result.append(" ").append(getSize(object));
			}
			if(object != this) {
				String objectToString = object.toString();
				if(! objectToString.startsWith(object.getClass().getName()+"@")) {
					result.append("<br/>").append(objectToString);
				} 
			}
			return result.toString();
		}
		
		private boolean hasSize(Object object) {
			try {
				Method sizeMethod = ReflectionUtil.getDeclaredMethod(object.getClass(), "size");
				return sizeMethod != null;
			} catch (Exception e) {
				return false;
			}
			
		}
		
		private String getSize(Object object) {
			try {
				Method sizeMethod = ReflectionUtil.getDeclaredMethod(object.getClass(), "size");
				return "(" + ((int) sizeMethod.invoke(object)) + " element(s))";
			} catch (Exception e) {
				return e.getClass().getName() + " " + e.getMessage() + " when invoking size()";
			}
		}
		
	}


}
