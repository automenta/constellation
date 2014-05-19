/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 * Copyright 2014 Geomatys.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.constellation.admin.web.controller;

import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;

import org.constellation.admin.conf.CstlConfig;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/conf")
public class ConfigController {
	
	@Inject
	private CstlConfig cstlConfig;

	@RequestMapping(method=RequestMethod.GET)
	public @ResponseBody
	Map<Object, Object> get() {
		Properties properties = new Properties();
		properties.put("cstl", cstlConfig.getUrl());
		return properties;
	}

}
