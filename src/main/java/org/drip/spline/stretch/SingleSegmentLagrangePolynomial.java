	
package org.drip.spline.stretch;

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
 * Copyright (C) 2016 Lakshmi Krishnamurthy
 * Copyright (C) 2015 Lakshmi Krishnamurthy
 * Copyright (C) 2014 Lakshmi Krishnamurthy
 * Copyright (C) 2013 Lakshmi Krishnamurthy
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
 * <i>SingleSegmentLagrangePolynomial</i> implements the SingleSegmentSequence Stretch interface using the
 * Lagrange Polynomial Estimator. As such it provides a perfect fit that travels through all the
 * predictor/response pairs causing Runge's instability.
 *
 * <br><br>
 *  <ul>
 *		<li><b>Module </b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></li>
 *		<li><b>Library</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/SplineBuilderLibrary.md">Spline Builder Library</a></li>
 *		<li><b>Project</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/spline/README.md">Basis Splines and Linear Compounders across a Broad Family of Spline Basis Functions</a></li>
 *		<li><b>Package</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/spline/stretch/README.md">Multi-Segment Sequence Spline Stretch</a></li>
 *  </ul>
 * <br><br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class SingleSegmentLagrangePolynomial implements org.drip.spline.stretch.SingleSegmentSequence {
	private static final double DIFF_SCALE = 1.0e-06;
	private static final int MAXIMA_PREDICTOR_ORDINATE_NODE = 1;
	private static final int MINIMA_PREDICTOR_ORDINATE_NODE = 2;
	private static final int MONOTONE_PREDICTOR_ORDINATE_NODE = 4;

	private double[] _adblResponseValue = null;
	private double[] _adblPredictorOrdinate = null;

	private static final double CalcAbsoluteMin (
		final double[] adblY)
		throws java.lang.Exception
	{
		if (null == adblY)
			throw new java.lang.Exception
				("SingleSegmentLagrangePolynomial::CalcAbsoluteMin => Invalid Inputs");

		int iNumPoints = adblY.length;

		if (1 >= iNumPoints)
			throw new java.lang.Exception
				("SingleSegmentLagrangePolynomial::CalcAbsoluteMin => Invalid Inputs");

		double dblMin = java.lang.Math.abs (adblY[0]);

		for (int i = 0; i < iNumPoints; ++i) {
			double dblValue = java.lang.Math.abs (adblY[i]);

			dblMin = dblMin > dblValue ? dblValue : dblMin;
		}

		return dblMin;
	}

	private static final double CalcMinDifference (
		final double[] adblY)
		throws java.lang.Exception
	{
		if (null == adblY)
			throw new java.lang.Exception
				("SingleSegmentLagrangePolynomial::CalcMinDifference => Invalid Inputs");

		int iNumPoints = adblY.length;

		if (1 >= iNumPoints)
			throw new java.lang.Exception
				("SingleSegmentLagrangePolynomial::CalcMinDifference => Invalid Inputs");

		double dblMinDiff = java.lang.Math.abs (adblY[0] - adblY[1]);

		for (int i = 0; i < iNumPoints; ++i) {
			for (int j = i + 1; j < iNumPoints; ++j) {
				double dblDiff = java.lang.Math.abs (adblY[i] - adblY[j]);

				dblMinDiff = dblMinDiff > dblDiff ? dblDiff : dblMinDiff;
			}
		}

		return dblMinDiff;
	}

	private static final double EstimateBumpDelta (
		final double[] adblY)
		throws java.lang.Exception
	{
		double dblBumpDelta = CalcMinDifference (adblY);

		if (!org.drip.numerical.common.NumberUtil.IsValid (dblBumpDelta) || 0. == dblBumpDelta)
			dblBumpDelta = CalcAbsoluteMin (adblY);

		return 0. == dblBumpDelta ? DIFF_SCALE : dblBumpDelta * DIFF_SCALE;
	}

	/**
	 * SingleSegmentLagrangePolynomial constructor
	 * 
	 * @param adblPredictorOrdinate Array of Predictor Ordinates
	 * 
	 * @throws java.lang.Exception Thrown if inputs are invalid
	 */

	public SingleSegmentLagrangePolynomial (
		final double[] adblPredictorOrdinate)
		throws java.lang.Exception
	{
		if (null == (_adblPredictorOrdinate = adblPredictorOrdinate))
			throw new java.lang.Exception ("SingleSegmentLagrangePolynomial ctr: Invalid Inputs");

		int iNumPredictorOrdinate = _adblPredictorOrdinate.length;

		if (1 >= iNumPredictorOrdinate)
			throw new java.lang.Exception ("SingleSegmentLagrangePolynomial ctr: Invalid Inputs");

		for (int i = 0; i < iNumPredictorOrdinate; ++i) {
			for (int j = i + 1; j < iNumPredictorOrdinate; ++j) {
				if (_adblPredictorOrdinate[i] == _adblPredictorOrdinate[j])
					throw new java.lang.Exception ("SingleSegmentLagrangePolynomial ctr: Invalid Inputs");
			}
		}
	}

	@Override public boolean setup (
		final double dblYLeading,
		final double[] adblResponseValue,
		final org.drip.spline.params.StretchBestFitResponse rbfr,
		final org.drip.spline.stretch.BoundarySettings bs,
		final int iCalibrationDetail)
	{
		return null != (_adblResponseValue = adblResponseValue) && _adblResponseValue.length ==
			_adblPredictorOrdinate.length;
	}

	@Override public double responseValue (
		final double dblPredictorOrdinate)
		throws java.lang.Exception
	{
		if (!org.drip.numerical.common.NumberUtil.IsValid (dblPredictorOrdinate))
			throw new java.lang.Exception
				("SingleSegmentLagrangePolynomial::responseValue => Invalid inputs!");

		int iNumPredictorOrdinate = _adblPredictorOrdinate.length;

		if (_adblPredictorOrdinate[0] > dblPredictorOrdinate ||
			_adblPredictorOrdinate[iNumPredictorOrdinate - 1] < dblPredictorOrdinate)
			throw new java.lang.Exception
				("SingleSegmentLagrangePolynomial::responseValue => Input out of range!");

		double dblResponse = 0;

		for (int i = 0; i < iNumPredictorOrdinate; ++i) {
			double dblResponsePredictorOrdinateContribution = _adblResponseValue[i];

			for (int j = 0; j < iNumPredictorOrdinate; ++j) {
				if (i != j)
					dblResponsePredictorOrdinateContribution = dblResponsePredictorOrdinateContribution *
						(dblPredictorOrdinate - _adblPredictorOrdinate[j]) / (_adblPredictorOrdinate[i] -
							_adblPredictorOrdinate[j]);
			}

			dblResponse += dblResponsePredictorOrdinateContribution;
		}

		return dblResponse;
	}

	@Override public double responseValueDerivative (
		final double dblPredictorOrdinate,
		final int iOrder)
		throws java.lang.Exception
	{
		if (!org.drip.numerical.common.NumberUtil.IsValid (dblPredictorOrdinate) || 0 >= iOrder)
			throw new java.lang.Exception
				("SingleSegmentLagrangePolynomial::responseValueDerivative => Invalid inputs!");

		org.drip.function.definition.R1ToR1 au = new org.drip.function.definition.R1ToR1
			(null) {
			@Override public double evaluate (
				double dblX)
				throws java.lang.Exception
			{
				return responseValue (dblX);
			}
		};

		return au.derivative (dblPredictorOrdinate, iOrder);
	}

	@Override public org.drip.numerical.differentiation.WengertJacobian jackDResponseDCalibrationInput (
		final double dblPredictorOrdinate,
		final int iOrder)
	{
		if (!org.drip.numerical.common.NumberUtil.IsValid (dblPredictorOrdinate)) return null;

		int iNumPredictorOrdinate = _adblPredictorOrdinate.length;
		double dblInputResponseSensitivityShift = java.lang.Double.NaN;
		double dblResponseWithUnadjustedResponseInput = java.lang.Double.NaN;
		org.drip.numerical.differentiation.WengertJacobian wjDResponseDResponseInput = null;

		if (_adblPredictorOrdinate[0] > dblPredictorOrdinate ||
			_adblPredictorOrdinate[iNumPredictorOrdinate - 1] < dblPredictorOrdinate)
			return null;

		try {
			if (!org.drip.numerical.common.NumberUtil.IsValid (dblInputResponseSensitivityShift =
				EstimateBumpDelta (_adblResponseValue)) || !org.drip.numerical.common.NumberUtil.IsValid
					(dblResponseWithUnadjustedResponseInput = responseValue (dblPredictorOrdinate)))
				return null;

			wjDResponseDResponseInput = new org.drip.numerical.differentiation.WengertJacobian (1,
				iNumPredictorOrdinate);
		} catch (java.lang.Exception e) {
			e.printStackTrace();

			return null;
		}

		for (int i = 0; i < iNumPredictorOrdinate; ++i) {
			double[] adblSensitivityShiftedInputResponse = new double[iNumPredictorOrdinate];

			for (int j = 0; j < iNumPredictorOrdinate; ++j)
				adblSensitivityShiftedInputResponse[j] = i == j ? _adblResponseValue[j] +
					dblInputResponseSensitivityShift : _adblResponseValue[j];

			try {
				SingleSegmentLagrangePolynomial lps = new SingleSegmentLagrangePolynomial
					(_adblPredictorOrdinate);

				if (!lps.setup (adblSensitivityShiftedInputResponse[0], adblSensitivityShiftedInputResponse,
					null, org.drip.spline.stretch.BoundarySettings.FloatingStandard(),
						org.drip.spline.stretch.MultiSegmentSequence.CALIBRATE) ||
							!wjDResponseDResponseInput.accumulatePartialFirstDerivative (0, i,
								(lps.responseValue (dblPredictorOrdinate) -
									dblResponseWithUnadjustedResponseInput) /
										dblInputResponseSensitivityShift))
					return null;
			} catch (java.lang.Exception e) {
				e.printStackTrace();

				return null;
			}
		}

		return wjDResponseDResponseInput;
	}

	@Override public org.drip.numerical.differentiation.WengertJacobian jackDResponseDManifestMeasure (
		final java.lang.String strManifestMeasure,
		final double dblPredictorOrdinate,
		final int iOrder)
	{
		return null;
	}

	@Override public org.drip.spline.segment.Monotonocity monotoneType (
		final double dblPredictorOrdinate)
	{
		if (!org.drip.numerical.common.NumberUtil.IsValid (dblPredictorOrdinate)) return null;

		int iNumPredictorOrdinate = _adblPredictorOrdinate.length;

		if (_adblPredictorOrdinate[0] > dblPredictorOrdinate ||
			_adblPredictorOrdinate[iNumPredictorOrdinate - 1] < dblPredictorOrdinate)
			return null;

		if (2 == iNumPredictorOrdinate) {
			try {
				return new org.drip.spline.segment.Monotonocity
					(org.drip.spline.segment.Monotonocity.MONOTONIC);
			} catch (java.lang.Exception e) {
				e.printStackTrace();

				return null;
			}
		}

		org.drip.function.definition.R1ToR1 auDeriv = new
			org.drip.function.definition.R1ToR1 (null) {
			@Override public double evaluate (
				final double dblX)
				throws java.lang.Exception
			{
				double dblDeltaX = CalcMinDifference (_adblPredictorOrdinate) * DIFF_SCALE;

				return (responseValue (dblX + dblDeltaX) - responseValue (dblX)) / dblDeltaX;
			}

			@Override public double integrate (
				final double dblBegin,
				final double dblEnd)
				throws java.lang.Exception
			{
				return org.drip.numerical.integration.R1ToR1Integrator.Boole (this, dblBegin, dblEnd);
			}
		};

		try {
			org.drip.function.r1tor1solver.FixedPointFinderOutput fpop = new
				org.drip.function.r1tor1solver.FixedPointFinderBrent (0., auDeriv, true).findRoot
					(org.drip.function.r1tor1solver.InitializationHeuristics.FromHardSearchEdges (0., 1.));

			if (null == fpop || !fpop.containsRoot())
				return new org.drip.spline.segment.Monotonocity
					(org.drip.spline.segment.Monotonocity.MONOTONIC);

			double dblExtremum = fpop.getRoot();

			if (!org.drip.numerical.common.NumberUtil.IsValid (dblExtremum) || dblExtremum <= 0. || dblExtremum
				>= 1.)
				return new org.drip.spline.segment.Monotonocity
					(org.drip.spline.segment.Monotonocity.MONOTONIC);

			double dblDeltaX = CalcMinDifference (_adblPredictorOrdinate) * DIFF_SCALE;

			double dbl2ndDeriv = responseValue (dblExtremum + dblDeltaX) + responseValue (dblExtremum -
				dblDeltaX) - 2. * responseValue (dblPredictorOrdinate);

			if (0. > dbl2ndDeriv)
				return new org.drip.spline.segment.Monotonocity
					(org.drip.spline.segment.Monotonocity.MAXIMA);

			if (0. < dbl2ndDeriv)
				return new org.drip.spline.segment.Monotonocity
					(org.drip.spline.segment.Monotonocity.MINIMA);

			if (0. == dbl2ndDeriv)
				return new org.drip.spline.segment.Monotonocity
					(org.drip.spline.segment.Monotonocity.INFLECTION);

			return new org.drip.spline.segment.Monotonocity
				(org.drip.spline.segment.Monotonocity.NON_MONOTONIC);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		try {
			return new org.drip.spline.segment.Monotonocity
				(org.drip.spline.segment.Monotonocity.MONOTONIC);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override public boolean isLocallyMonotone()
		throws java.lang.Exception
	{
		org.drip.spline.segment.Monotonocity sm = monotoneType (0.5 * (_adblPredictorOrdinate[0] +
			_adblPredictorOrdinate[_adblPredictorOrdinate.length - 1]));

		return null != sm && org.drip.spline.segment.Monotonocity.MONOTONIC == sm.type();
	}

	@Override public boolean isCoMonotone (
		final double[] adblMeasuredResponse)
		throws java.lang.Exception
	{
		if (null == adblMeasuredResponse) return false;

		int iNumMeasuredResponse = adblMeasuredResponse.length;

		if (2 >= iNumMeasuredResponse) return false;

		int[] aiNodeMiniMax = new int[iNumMeasuredResponse];
		int[] aiMonotoneType = new int[iNumMeasuredResponse];

		for (int i = 0; i < iNumMeasuredResponse; ++i) {
			if (0 == i || iNumMeasuredResponse - 1 == i)
				aiNodeMiniMax[i] = 0;
			else {
				if (adblMeasuredResponse[i - 1] < adblMeasuredResponse[i] && adblMeasuredResponse[i + 1] <
					adblMeasuredResponse[i])
					aiNodeMiniMax[i] = MAXIMA_PREDICTOR_ORDINATE_NODE;
				else if (adblMeasuredResponse[i - 1] > adblMeasuredResponse[i] && adblMeasuredResponse[i + 1]
					> adblMeasuredResponse[i])
					aiNodeMiniMax[i] = MINIMA_PREDICTOR_ORDINATE_NODE;
				else
					aiNodeMiniMax[i] = MONOTONE_PREDICTOR_ORDINATE_NODE;
			}

			org.drip.spline.segment.Monotonocity sm = monotoneType (adblMeasuredResponse[i]);

			aiMonotoneType[i] = null != sm ? sm.type() :
				org.drip.spline.segment.Monotonocity.NON_MONOTONIC;
		}

		for (int i = 1; i < iNumMeasuredResponse - 1; ++i) {
			if (MAXIMA_PREDICTOR_ORDINATE_NODE == aiNodeMiniMax[i]) {
				if (org.drip.spline.segment.Monotonocity.MAXIMA != aiMonotoneType[i] &&
					org.drip.spline.segment.Monotonocity.MAXIMA != aiMonotoneType[i - 1])
					return false;
			} else if (MINIMA_PREDICTOR_ORDINATE_NODE == aiNodeMiniMax[i]) {
				if (org.drip.spline.segment.Monotonocity.MINIMA != aiMonotoneType[i] &&
					org.drip.spline.segment.Monotonocity.MINIMA != aiMonotoneType[i - 1])
					return false;
			}
		}

		return true;
	}

	@Override public boolean isKnot (
		final double dblPredictorOrdinate)
	{
		if (!org.drip.numerical.common.NumberUtil.IsValid (dblPredictorOrdinate)) return false;

		int iNumPredictorOrdinate = _adblPredictorOrdinate.length;

		if (_adblPredictorOrdinate[0] > dblPredictorOrdinate ||
			_adblPredictorOrdinate[iNumPredictorOrdinate - 1] < dblPredictorOrdinate)
			return false;

		for (int i = 0; i < iNumPredictorOrdinate; ++i) {
			if (dblPredictorOrdinate == _adblPredictorOrdinate[i]) return true;
		}

		return false;
	}

	@Override public boolean resetNode (
		final int iPredictorOrdinateNodeIndex,
		final double dblResetResponse)
	{
		if (!org.drip.numerical.common.NumberUtil.IsValid (dblResetResponse)) return false;

		if (iPredictorOrdinateNodeIndex > _adblPredictorOrdinate.length) return false;

		_adblResponseValue[iPredictorOrdinateNodeIndex] = dblResetResponse;
		return true;
	}

	@Override public boolean resetNode (
		final int iPredictorOrdinateNodeIndex,
		final org.drip.spline.params.SegmentResponseValueConstraint sprcReset)
	{
		return false;
	}

	@Override public org.drip.function.definition.R1ToR1 toAU()
	{
		org.drip.function.definition.R1ToR1 au = new
			org.drip.function.definition.R1ToR1 (null)
		{
			@Override public double evaluate (
				final double dblVariate)
				throws java.lang.Exception
			{
				return responseValue (dblVariate);
			}

			@Override public double derivative (
				final double dblVariate,
				final int iOrder)
				throws java.lang.Exception
			{
				return responseValueDerivative (dblVariate, iOrder);
			}
		};

		return au;
	}

	@Override public double getLeftPredictorOrdinateEdge()
	{
		return _adblPredictorOrdinate[0];
	}

	@Override public double getRightPredictorOrdinateEdge()
	{
		return _adblPredictorOrdinate[_adblPredictorOrdinate.length - 1];
	}
}
