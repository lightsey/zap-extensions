/*
 * Zed Attack Proxy (ZAP) and its related class files.
 *
 * ZAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zaproxy.zap.extension.pscanrules;

import java.util.Vector;

import net.htmlparser.jericho.Source;

import org.parosproxy.paros.Constant;
import org.parosproxy.paros.core.scanner.Alert;
import org.parosproxy.paros.network.HttpHeader;
import org.parosproxy.paros.network.HttpMessage;
import org.zaproxy.zap.extension.pscan.PassiveScanThread;
import org.zaproxy.zap.extension.pscan.PluginPassiveScanner;

public class ContentTypeMissingScanner extends PluginPassiveScanner {

	/**
	 * Prefix for internationalised messages used by this rule
	 */
	private static final String MESSAGE_PREFIX = "pscanrules.contenttypemissingscanner.";
	
	private PassiveScanThread parent = null;
	
	@Override
	public void scanHttpRequestSend(HttpMessage msg, int id) {
		
		
	}

	@Override
	public void scanHttpResponseReceive(HttpMessage msg, int id, Source source) {
		if (msg.getResponseBody().length() > 0) {
			Vector<String> contentType = msg.getResponseHeader().getHeaders(HttpHeader.CONTENT_TYPE);
				if (contentType != null) {
					for (String contentTypeDirective : contentType) {
						if (contentTypeDirective.isEmpty()) {
							this.raiseAlert(msg, id, contentTypeDirective, false);
						}
					}
				} else {
					this.raiseAlert(msg, id, "", true);
				}
		}
	}
		
	private void raiseAlert(HttpMessage msg, int id, String contentType, boolean isContentTypeMissing) {
		String issue = "Content-Type header empty";
		if (isContentTypeMissing){
			issue = "Content-Type header missing";
		}
		
		Alert alert = new Alert(getPluginId(), Alert.RISK_LOW, Alert.CONFIDENCE_MEDIUM, 
		    	getName());
		    	alert.setDetail(
		    		issue,
		    	    msg.getRequestHeader().getURI().toString(),
		    	    contentType,
		    	    "", 
		    	    "", 
		    	    "Ensure each page is setting the specific and appropriate content-type value for the content being delivered", 
		            "http://msdn.microsoft.com/en-us/library/ie/gg622941%28v=vs.85%29.aspx", 
		            "", // No evidence
		            0,	// TODO CWE Id
		            0,	// TODO WASC Id
		            msg);
	
    	parent.raiseAlert(id, alert);
	}
		

	@Override
	public void setParent(PassiveScanThread parent) {
			this.parent = parent;
	}

	@Override
	public String getName() {
		return Constant.messages.getString(MESSAGE_PREFIX + "name");
	}
	
	@Override
	public int getPluginId() {
		return 10019;
	}

}
