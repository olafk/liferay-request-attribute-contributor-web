package com.liferay.sales.demo.context.contributor;

import com.liferay.portal.kernel.template.TemplateContextContributor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;

/**
 * A simple POC class that outputs all available request attributes.
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
				return object.getClass().getName();
			}
			return "<i>null</i>";
		}
	}

}
