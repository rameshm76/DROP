
package org.drip.service.json;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2019 Lakshmi Krishnamurthy
 * Copyright (C) 2018 Lakshmi Krishnamurthy
 * Copyright (C) 2017 Lakshmi Krishnamurthy
 * Copyright (C) 2016 Lakshmi Krishnamurthy
 * 
 *  This file is part of DROP, an open-source library targeting risk, transaction costs, exposure, margin
 *  	calculations, and portfolio construction within and across fixed income, credit, commodity, equity,
 *  	FX, and structured products.
 *  
 *  	https://lakshmidrip.github.io/DROP/
 *  
 *  DROP is composed of three main modules:
 *  
 *  - DROP Analytics Core - https://lakshmidrip.github.io/DROP-Analytics-Core/
 *  - DROP Portfolio Core - https://lakshmidrip.github.io/DROP-Portfolio-Core/
 *  - DROP Numerical Core - https://lakshmidrip.github.io/DROP-Numerical-Core/
 * 
 * 	DROP Analytics Core implements libraries for the following:
 * 	- Fixed Income Analytics
 * 	- Asset Backed Analytics
 * 	- XVA Analytics
 * 	- Exposure and Margin Analytics
 * 
 * 	DROP Portfolio Core implements libraries for the following:
 * 	- Asset Allocation Analytics
 * 	- Transaction Cost Analytics
 * 
 * 	DROP Numerical Core implements libraries for the following:
 * 	- Statistical Learning Library
 * 	- Numerical Optimizer Library
 * 	- Machine Learning Library
 * 	- Spline Builder Library
 * 
 * 	Documentation for DROP is Spread Over:
 * 
 * 	- Main                     => https://lakshmidrip.github.io/DROP/
 * 	- Wiki                     => https://github.com/lakshmiDRIP/DROP/wiki
 * 	- GitHub                   => https://github.com/lakshmiDRIP/DROP
 * 	- Javadoc                  => https://lakshmidrip.github.io/DROP/Javadoc/index.html
 * 	- Technical Specifications => https://github.com/lakshmiDRIP/DROP/tree/master/Docs/Internal
 * 	- Release Versions         => https://lakshmidrip.github.io/DROP/version.html
 * 	- Community Credits        => https://lakshmidrip.github.io/DROP/credits.html
 * 	- Issues Catalog           => https://github.com/lakshmiDRIP/DROP/issues
 * 	- JUnit                    => https://lakshmidrip.github.io/DROP/junit/index.html
 * 	- Jacoco                   => https://lakshmidrip.github.io/DROP/jacoco/index.html
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *   	you may not use this file except in compliance with the License.
 *   
 *  You may obtain a copy of the License at
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  	distributed under the License is distributed on an "AS IS" BASIS,
 *  	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  
 *  See the License for the specific language governing permissions and
 *  	limitations under the License.
 */

/**
 * <i>CreditDefaultSwapProcessor</i> Sets Up and Executes a JSON Based In/Out Credit Default Swap Valuation
 * Processor.
 * 
 * <br><br>
 *  <ul>
 *		<li><b>Project</b>       = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/service">Service</a></li>
 *		<li><b>Package</b>       = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/service/json">JSON</a></li>
 *		<li><b>Specification</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/Docs/Internal/NumericalOptimizer">Numerical Optimizer Library</a></li>
 *  </ul>
 * <br><br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class CreditDefaultSwapProcessor {

	/**
	 * JSON Based in/out Credit Default Swap Curve Metrics Thunker
	 * 
	 * @param jsonParameter JSON Credit Default Swap Request Parameters
	 * 
	 * @return JSON Credit Default Swap Curve Metrics Response
	 */

	@SuppressWarnings ("unchecked") static final org.drip.json.simple.JSONObject CurveMetrics (
		final org.drip.json.simple.JSONObject jsonParameter)
	{
		org.drip.state.discount.MergedDiscountForwardCurve dcFunding =
			org.drip.service.json.LatentStateProcessor.FundingCurve (jsonParameter);

		org.drip.state.credit.CreditCurve ccSurvivalRecovery =
			org.drip.service.json.LatentStateProcessor.CreditCurve (jsonParameter, dcFunding);

		if (null == ccSurvivalRecovery) return null;

		org.drip.param.market.CurveSurfaceQuoteContainer csqc = new
			org.drip.param.market.CurveSurfaceQuoteContainer();

		if (!csqc.setFundingState (dcFunding) || !csqc.setCreditState (ccSurvivalRecovery)) return null;

		org.drip.analytics.date.JulianDate dtSpot = dcFunding.epoch();

		org.drip.product.definition.CreditDefaultSwap cds = null;

		try {
			cds = org.drip.service.template.OTCInstrumentBuilder.CDS (dtSpot,
				org.drip.json.parser.Converter.StringEntry (jsonParameter, "CDSMaturity"),
					org.drip.json.parser.Converter.DoubleEntry (jsonParameter, "CDSCoupon"),
						dcFunding.currency(), ((org.drip.state.identifier.EntityCDSLabel)
							(ccSurvivalRecovery.label())).referenceEntity());
		} catch (java.lang.Exception e) {
			e.printStackTrace();

			return null;
		}

		if (null == cds) return null;

		java.util.Map<java.lang.String, java.lang.Double> mapResult = cds.value
			(org.drip.param.valuation.ValuationParams.Spot (dtSpot.julian()), null, csqc, null);

		if (null == mapResult) return null;

		org.drip.json.simple.JSONObject jsonResponse = new org.drip.json.simple.JSONObject();

		for (java.util.Map.Entry<java.lang.String, java.lang.Double> me : mapResult.entrySet())
			jsonResponse.put (me.getKey(), me.getValue());

		org.drip.json.simple.JSONArray jsonCouponFlowArray = new org.drip.json.simple.JSONArray();

		for (org.drip.analytics.cashflow.CompositePeriod cp : cds.couponPeriods()) {
			org.drip.json.simple.JSONObject jsonCouponFlow = new org.drip.json.simple.JSONObject();

			try {
				jsonCouponFlow.put ("StartDate", new org.drip.analytics.date.JulianDate
					(cp.startDate()).toString());

				jsonCouponFlow.put ("EndDate", new org.drip.analytics.date.JulianDate
					(cp.endDate()).toString());

				jsonCouponFlow.put ("PayDate", new org.drip.analytics.date.JulianDate
					(cp.payDate()).toString());

				jsonCouponFlow.put ("CouponDCF", cp.couponDCF());

				jsonCouponFlow.put ("PayDiscountFactor", cp.df (csqc));

				jsonCouponFlow.put ("SurvivalProbability", cp.survival (csqc));
			} catch (java.lang.Exception e) {
				e.printStackTrace();

				return null;
			}

			jsonCouponFlow.put ("BaseNotional", cp.baseNotional());

			jsonCouponFlow.put ("Tenor", cp.tenor());

			jsonCouponFlow.put ("FundingLabel", cp.fundingLabel().fullyQualifiedName());

			jsonCouponFlow.put ("CreditLabel", cp.creditLabel().fullyQualifiedName());

			jsonCouponFlow.put ("ReferenceRate", cp.couponMetrics (dtSpot.julian(), csqc).rate());

			jsonCouponFlowArray.add (jsonCouponFlow);
		}

		jsonResponse.put ("CouponFlow", jsonCouponFlowArray);

		org.drip.json.simple.JSONArray jsonLossFlowArray = new org.drip.json.simple.JSONArray();

		for (org.drip.analytics.cashflow.LossQuadratureMetrics lqm : cds.lossFlow (dtSpot, csqc)) {
			org.drip.json.simple.JSONObject jsonLossFlow = new org.drip.json.simple.JSONObject();

			try {
				jsonLossFlow.put ("StartDate", new org.drip.analytics.date.JulianDate
					(lqm.startDate()).toString());

				jsonLossFlow.put ("EndDate", new org.drip.analytics.date.JulianDate
					(lqm.endDate()).toString());
			} catch (java.lang.Exception e) {
				e.printStackTrace();

				return null;
			}

			jsonLossFlow.put ("StartSurvival", lqm.startSurvival());

			jsonLossFlow.put ("EndSurvival", lqm.endSurvival());

			jsonLossFlow.put ("EffectiveNotional", lqm.effectiveNotional());

			jsonLossFlow.put ("EffectiveRecovery", lqm.effectiveRecovery());

			jsonLossFlow.put ("EffectiveAccrual", lqm.accrualDCF());

			jsonLossFlow.put ("EffectiveDF", lqm.effectiveDF());

			jsonLossFlowArray.add (jsonLossFlow);
		}

		jsonResponse.put ("LossFlow", jsonLossFlowArray);

		return jsonResponse;
	}
}
