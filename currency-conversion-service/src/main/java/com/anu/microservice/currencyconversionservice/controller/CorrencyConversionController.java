package com.anu.microservice.currencyconversionservice.controller;

import java.math.BigDecimal;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.support.HttpRequestHandlerServlet;

import com.anu.microservice.currencyconversionservice.bean.CurrencyConversion;
import com.anu.microservice.currencyconversionservice.bean.CurrencyExchangeProxy;

@RestController
public class CorrencyConversionController {

	@Autowired
	private CurrencyExchangeProxy proxy;
	
	@LoadBalanced
	@GetMapping("/currency-conversion/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversion calculateCurrencyConversion(
			@PathVariable String from,
			@PathVariable String to,
			@PathVariable BigDecimal quantity) {
		  HashMap<String,String> uriVariable = new HashMap<>();
		  uriVariable.put("from", from);
		  uriVariable.put("to", to);
		
		ResponseEntity<CurrencyConversion> responseEntity =  new RestTemplate().getForEntity("http://localhost:8000/currency-exchange/from/{from}/to/{to}", 
				CurrencyConversion.class,uriVariable);
		CurrencyConversion currencyConversion = responseEntity.getBody();
		
		return new CurrencyConversion(currencyConversion.getId(), from, to, quantity,
				currencyConversion.getConversionMultiple(), quantity.multiply(currencyConversion.getConversionMultiple()),
				currencyConversion.getEnvironment()+ "  rest");
		
	}
	
	@GetMapping("/currency-conversion-feign/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversion calculateCurrencyConversionFeign(
			@PathVariable String from,
			@PathVariable String to,
			@PathVariable BigDecimal quantity) {
		  
		CurrencyConversion currencyConversion = proxy.retriveExchangeValue(from, to);
		
		return new CurrencyConversion(currencyConversion.getId(), from, to, quantity,
				currencyConversion.getConversionMultiple(), quantity.multiply(currencyConversion.getConversionMultiple()),
				currencyConversion.getEnvironment()+" feign");
		
	}
}
