/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primefaces.component.hotkey;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.ActionEvent;

import org.primefaces.context.PrimeRequestContext;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.AjaxRequestBuilder;

public class HotkeyRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext facesContext, UIComponent component) {
        Map<String, String> params = facesContext.getExternalContext().getRequestParameterMap();
        Hotkey hotkey = (Hotkey) component;

        if (params.containsKey(hotkey.getClientId(facesContext))) {
            hotkey.queueEvent(new ActionEvent(hotkey));
        }
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Hotkey hotkey = (Hotkey) component;
        String clientId = hotkey.getClientId(context);

        writer.startElement("script", null);
        writer.writeAttribute("type", "text/javascript", null);

        String event = "keydown." + clientId;

        writer.write("$(function(){");
        writer.write("$(document).off('" + event + "').on('" + event + "',null,'" + hotkey.getBind() + "',function(){");

        if (hotkey.isAjaxified()) {
            AjaxRequestBuilder builder = PrimeRequestContext.getCurrentInstance(context).getAjaxRequestBuilder();

            String request = builder.init()
                    .source(clientId)
                    .form(hotkey, hotkey)
                    .process(component, hotkey.getProcess())
                    .update(component, hotkey.getUpdate())
                    .async(hotkey.isAsync())
                    .global(hotkey.isGlobal())
                    .delay(hotkey.getDelay())
                    .timeout(hotkey.getTimeout())
                    .partialSubmit(hotkey.isPartialSubmit(), hotkey.isPartialSubmitSet(), hotkey.getPartialSubmitFilter())
                    .resetValues(hotkey.isResetValues(), hotkey.isResetValuesSet())
                    .ignoreAutoUpdate(hotkey.isIgnoreAutoUpdate())
                    .onstart(hotkey.getOnstart())
                    .onerror(hotkey.getOnerror())
                    .onsuccess(hotkey.getOnsuccess())
                    .oncomplete(hotkey.getOncomplete())
                    .params(hotkey)
                    .build();

            writer.write(request);
        }
        else {
            writer.write(hotkey.getHandler());
        }

        writer.write(";return false;});});");

        writer.endElement("script");
    }
}
