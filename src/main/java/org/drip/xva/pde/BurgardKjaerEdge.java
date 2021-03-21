
package org.drip.xva.pde;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2022 Lakshmi Krishnamurthy
 * Copyright (C) 2021 Lakshmi Krishnamurthy
 * Copyright (C) 2020 Lakshmi Krishnamurthy
 * Copyright (C) 2019 Lakshmi Krishnamurthy
 * Copyright (C) 2018 Lakshmi Krishnamurthy
 * Copyright (C) 2017 Lakshmi Krishnamurthy
 * 
 *  This file is part of DROP, an open-source library targeting analytics/risk, transaction cost analytics,
 *  	asset liability management analytics, capital, exposure, and margin analytics, valuation adjustment
 *  	analytics, and portfolio construction analytics within and across fixed income, credit, commodity,
 *  	equity, FX, and structured products. It also includes auxiliary libraries for algorithm support,
 *  	numerical analysis, numerical optimization, spline builder, model validation, statistical learning,
 *  	graph builder/navigator, and computational support.
 *  
 *  	https://lakshmidrip.github.io/DROP/
 *  
 *  DROP is composed of three modules:
 *  
 *  - DROP Product Core - https://lakshmidrip.github.io/DROP-Product-Core/
 *  - DROP Portfolio Core - https://lakshmidrip.github.io/DROP-Portfolio-Core/
 *  - DROP Computational Core - https://lakshmidrip.github.io/DROP-Computational-Core/
 * 
 * 	DROP Product Core implements libraries for the following:
 * 	- Fixed Income Analytics
 * 	- Loan Analytics
 * 	- Transaction Cost Analytics
 * 
 * 	DROP Portfolio Core implements libraries for the following:
 * 	- Asset Allocation Analytics
 *  - Asset Liability Management Analytics
 * 	- Capital Estimation Analytics
 * 	- Exposure Analytics
 * 	- Margin Analytics
 * 	- XVA Analytics
 * 
 * 	DROP Computational Core implements libraries for the following:
 * 	- Algorithm Support
 * 	- Computation Support
 * 	- Function Analysis
 *  - Graph Algorithm
 *  - Model Validation
 * 	- Numerical Analysis
 * 	- Numerical Optimizer
 * 	- Spline Builder
 *  - Statistical Learning
 * 
 * 	Documentation for DROP is Spread Over:
 * 
 * 	- Main                     => https://lakshmidrip.github.io/DROP/
 * 	- Wiki                     => https://github.com/lakshmiDRIP/DROP/wiki
 * 	- GitHub                   => https://github.com/lakshmiDRIP/DROP
 * 	- Repo Layout Taxonomy     => https://github.com/lakshmiDRIP/DROP/blob/master/Taxonomy.md
 * 	- Javadoc                  => https://lakshmidrip.github.io/DROP/Javadoc/index.html
 * 	- Technical Specifications => https://github.com/lakshmiDRIP/DROP/tree/master/Docs/Internal
 * 	- Release Versions         => https://lakshmidrip.github.io/DROP/version.html
 * 	- Community Credits        => https://lakshmidrip.github.io/DROP/credits.html
 * 	- Issues Catalog           => https://github.com/lakshmiDRIP/DROP/issues
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
 * <i>BurgardKjaerEdge</i> holds the Underlier Stochastic and the Credit Risk Free Components of the XVA
 * Derivative Value Growth, as laid out in Burgard and Kjaer (2014). The References are:
 *
 *  <br><br>
 *  <ul>
 *  	<li>
 *  		Burgard, C., and M. Kjaer (2014): PDE Representations of Derivatives with Bilateral Counter-party
 *  			Risk and Funding Costs <i>Journal of Credit Risk</i> <b>7 (3)</b> 1-19
 *  	</li>
 *  	<li>
 *  		Cesari, G., J. Aquilina, N. Charpillon, X. Filipovic, G. Lee, and L. Manda (2009): <i>Modeling,
 *  			Pricing, and Hedging Counter-party Credit Exposure - A Technical Guide</i> <b>Springer
 *  			Finance</b> New York
 *  	</li>
 *  	<li>
 *  		Gregory, J. (2009): Being Two-faced over Counter-party Credit Risk <i>Risk</i> <b>20 (2)</b>
 *  			86-90
 *  	</li>
 *  	<li>
 *  		Li, B., and Y. Tang (2007): <i>Quantitative Analysis, Derivatives Modeling, and Trading
 *  			Strategies in the Presence of Counter-party Credit Risk for the Fixed Income Market</i>
 *  			<b>World Scientific Publishing</b> Singapore
 *  	</li>
 *  	<li>
 *  		Piterbarg, V. (2010): Funding Beyond Discounting: Collateral Agreements and Derivatives Pricing
 *  			<i>Risk</i> <b>21 (2)</b> 97-102
 *  	</li>
 *  </ul>
 *
 *  <br><br>
 *  <ul>
 *		<li><b>Module </b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/PortfolioCore.md">Portfolio Core Module</a></li>
 *		<li><b>Library</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/XVAAnalyticsLibrary.md">XVA Analytics Library</a></li>
 *		<li><b>Project</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/xva/README.md">Valuation Adjustments that account for Collateral, CC Credit/Debt and Funding Overhead</a></li>
 *		<li><b>Package</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/xva/pde/README.md">Burgard Kjaer PDE Evolution Scheme</a></li>
 *  </ul>
 * <br><br>
 * 
 * @author Lakshmi Krishnamurthy
 */

public abstract class BurgardKjaerEdge
{
	private double _positionValueBump = java.lang.Double.NaN;
	private double _derivativeXVACollateralGrowth = java.lang.Double.NaN;
	private double _derivativeXVAStochasticGrowth = java.lang.Double.NaN;
	private double _derivativeXVAStochasticGrowthUp = java.lang.Double.NaN;
	private double _derivativeXVAStochasticGrowthDown = java.lang.Double.NaN;

	protected BurgardKjaerEdge (
		final double positionValueBump,
		final double derivativeXVAStochasticGrowthDown,
		final double derivativeXVAStochasticGrowth,
		final double derivativeXVAStochasticGrowthUp,
		final double derivativeXVACollateralGrowth)
		throws java.lang.Exception
	{
		if (!org.drip.numerical.common.NumberUtil.IsValid (_positionValueBump = positionValueBump) ||
			!org.drip.numerical.common.NumberUtil.IsValid (_derivativeXVAStochasticGrowthDown =
				derivativeXVAStochasticGrowthDown) ||
			!org.drip.numerical.common.NumberUtil.IsValid (_derivativeXVAStochasticGrowth =
				derivativeXVAStochasticGrowth) ||
			!org.drip.numerical.common.NumberUtil.IsValid (_derivativeXVAStochasticGrowthUp =
				derivativeXVAStochasticGrowthUp) ||
			!org.drip.numerical.common.NumberUtil.IsValid (_derivativeXVACollateralGrowth =
				derivativeXVACollateralGrowth))
		{
			throw new java.lang.Exception ("BurgardKjaerEdge Constructor => Invalid Inputs");
		}
	}

	/**
	 * Retrieve the Position Value Bump
	 * 
	 * @return The Position Value Bump
	 */

	public double positionValueBump()
	{
		return _positionValueBump;
	}

	/**
	 * Retrieve the Stochastic Down Component of the Derivative XVA Value
	 * 
	 * @return The Stochastic Down Component of the Derivative XVA Value
	 */

	public double derivativeXVAStochasticGrowthDown()
	{
		return _derivativeXVAStochasticGrowthDown;
	}

	/**
	 * Retrieve the Stochastic Component of the Derivative XVA Value Growth
	 * 
	 * @return The Stochastic Component of the Derivative XVA Value Growth
	 */

	public double derivativeXVAStochasticGrowth()
	{
		return _derivativeXVAStochasticGrowth;
	}

	/**
	 * Retrieve the Stochastic Up Component of the Derivative XVA Value
	 * 
	 * @return The Stochastic Up Component of the Derivative XVA Value
	 */

	public double derivativeXVAStochasticGrowthUp()
	{
		return _derivativeXVAStochasticGrowthUp;
	}

	/**
	 * Retrieve the Collateral Component of the Derivative XVA Value Growth
	 * 
	 * @return The Collateral Component of the Derivative XVA Value Growth
	 */

	public double derivativeXVACollateralGrowth()
	{
		return _derivativeXVACollateralGrowth;
	}

	/**
	 * Compute the Gross Theta from Position Value Down
	 * 
	 * @return The Gross Theta from Position Value Down
	 */

	public abstract double thetaPositionValueDown();

	/**
	 * Compute the Gross Theta from Position Value Base
	 * 
	 * @return The Gross Theta from Position Value Base
	 */

	public abstract double theta();

	/**
	 * Compute the Gross Theta from Position Value Up
	 * 
	 * @return The Gross Theta from Position Value Up
	 */

	public abstract double thetaPositionValueUp();
}
