	
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
 * <i>MultiSegmentSequence</i> is the interface that exposes functionality that spans multiple segments. Its
 * derived instances hold the ordered segment sequence, the segment control parameters, and, if available,
 * the spanning Jacobian. MultiSegmentSequence exports the following group of functionality:
 *
 * <br><br>
 *  <ul>
 *  	<li>
 * 			Retrieve the Segments and their Builder Parameters
 *  	</li>
 *  	<li>
 * 			Compute the monotonicity details - segment/Stretch level monotonicity, co-monotonicity, local
 * 				monotonicity
 *  	</li>
 *  	<li>
 * 			Check if the Predictor Ordinate is in the Stretch Range, and return the segment index in that
 * 				case
 *  	</li>
 *  	<li>
 * 			Set up (i.e., calibrate) the individual Segments in the Stretch by specifying one/or more of the
 * 				node parameters and Target Constraints
 *  	</li>
 *  	<li>
 * 			Set up (i.e., calibrate) the individual Segment in the Stretch to the Target Segment Edge Values
 * 				and Constraints. This is also called the Hermite setup - where the segment boundaries are
 * 				entirely locally set
 *  	</li>
 *  	<li>
 * 			Generate a new Stretch by clipping all the Segments to the Left/Right of the specified Predictor
 * 				Ordinate. Smoothness Constraints will be maintained
 *  	</li>
 *  	<li>
 * 			Retrieve the Span Curvature/Length, and the Best Fit DPE's
 *  	</li>
 *  	<li>
 * 			Retrieve the Merge Stretch Manager
 *  	</li>
 *  	<li>
 *  		Display the Segments
 *  	</li>
 *  </ul>
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

public interface MultiSegmentSequence extends org.drip.spline.stretch.SingleSegmentSequence {

	/**
	 * Calibration Detail: Calibrate the Stretch as part of the set up
	 */

	public static final int CALIBRATE = 1;

	/**
	 * Calibration Detail: Calibrate the Stretch AND compute Jacobian as part of the set up
	 */

	public static final int CALIBRATE_JACOBIAN = 2;

	/**
	 * Retrieve the Stretch Name
	 * 
	 * @return The Stretch Name
	 */

	public abstract java.lang.String name();

	/**
	 * Retrieve the Segment Builder Parameters
	 * 
	 * @return The Segment Builder Parameters
	 */

	public abstract org.drip.spline.params.SegmentCustomBuilderControl[] segmentBuilderControl();

	/**
	 * Retrieve the Stretch Segments
	 * 
	 * @return The Stretch Segments
	 */

	public abstract org.drip.spline.segment.LatentStateResponseModel[] segments();

	/**
	 * Set up (i.e., calibrate) the individual Segment in the Stretch to the Target Segment Edge Values and
	 * 	Constraints. This is also called the Hermite setup - where the segment boundaries are entirely
	 * 	locally set.
	 * 
	 * @param aSPRDLeft Array of Left Segment Edge Values
	 * @param aSPRDRight Array of Right Segment Edge Values
	 * @param aaSRVC Double Array of Constraints - Outer Index corresponds to Segment Index, and the Inner
	 * 		Index to Constraint Array within each Segment
	 * @param sbfr Stretch Fitness Weighted Response
	 * @param iSetupMode Set up Mode (i.e., set up ITEP only, or fully calibrate the Stretch, or calibrate
	 * 	 	Stretch plus compute Jacobian)
	 * 
	 * @return TRUE - Set up was successful
	 */

	public abstract boolean setupHermite (
		final org.drip.spline.params.SegmentPredictorResponseDerivative[] aSPRDLeft,
		final org.drip.spline.params.SegmentPredictorResponseDerivative[] aSPRDRight,
		final org.drip.spline.params.SegmentResponseValueConstraint[][] aaSRVC,
		final org.drip.spline.params.StretchBestFitResponse sbfr,
		final int iSetupMode);

	/**
	 * Set the Slope at the left Edge of the Stretch
	 * 
	 * @param dblStretchLeftResponse Response Value at the Left Edge of the Stretch
	 * @param dblStretchLeftResponseSlope Response Slope Value at the Left Edge of the Stretch
	 * @param dblStretchRightResponse Response Value at the Right Edge of the Stretch
	 * @param sbfr Stretch Fitness Weighted Response
	 * 
	 * @return TRUE - Left slope successfully set
	 */

	public abstract boolean setLeftNode (
		final double dblStretchLeftResponse,
		final double dblStretchLeftResponseSlope,
		final double dblStretchRightResponse,
		final org.drip.spline.params.StretchBestFitResponse sbfr);

	/**
	 * Calculate the SPRD at the specified Predictor Ordinate
	 * 
	 * @param dblPredictorOrdinate The Predictor Ordinate
	 * 
	 * @return The Computed SPRD
	 */

	public abstract org.drip.spline.params.SegmentPredictorResponseDerivative calcSPRD (
		final double dblPredictorOrdinate);

	/**
	 * Calculate the Derivative of the requested order at the Left Edge of the Stretch
	 * 
	 * @param iOrder Order of the Derivative
	 * 
	 * @return The Derivative of the requested order at the Left Edge of the Stretch
	 * 
	 * @throws java.lang.Exception Thrown if the Derivative cannot be calculated
	 */

	public abstract double calcLeftEdgeDerivative (
		final int iOrder)
		throws java.lang.Exception;

	/**
	 * Calculate the Derivative of the requested order at the right Edge of the Stretch
	 * 
	 * @param iOrder Order of the Derivative
	 * 
	 * @return The Derivative of the requested order at the right Edge of the Stretch
	 * 
	 * @throws java.lang.Exception Thrown if the Derivative cannot be calculated
	 */

	public abstract double calcRightEdgeDerivative (
		final int iOrder)
		throws java.lang.Exception;

	/**
	 * Check if the Predictor Ordinate is in the Stretch Range
	 * 
	 * @param dblPredictorOrdinate Predictor Ordinate
	 * 
	 * @return TRUE - Predictor Ordinate is in the Range
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are invalid
	 */

	public abstract boolean in (
		final double dblPredictorOrdinate)
		throws java.lang.Exception;

	/**
	 * Return the Index for the Segment containing specified Predictor Ordinate
	 * 
	 * @param dblPredictorOrdinate Predictor Ordinate
	 * @param bIncludeLeft TRUE - Less than or equal to the Left Predictor Ordinate
	 * @param bIncludeRight TRUE - Less than or equal to the Right Predictor Ordinate
	 * 
	 * @return Index for the Segment containing specified Predictor Ordinate
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are invalid
	 */

	public int containingIndex (
		final double dblPredictorOrdinate,
		final boolean bIncludeLeft,
		final boolean bIncludeRight)
		throws java.lang.Exception;

	/**
	 * Set up (i.e., calibrate) the individual Segments in the Stretch to the Stretch Edge, the Target
	 *  Constraints, and the custom segment sequence builder.
	 * 
	 * @param ssb The Segment Sequence Builder Instance
	 * @param iCalibrationDetail The Calibration Detail
	 * 
	 * @return TRUE - Set up was successful
	 */

	public abstract boolean setup (
		final org.drip.spline.stretch.SegmentSequenceBuilder ssb,
		final int iCalibrationDetail);

	/**
	 * Set up (i.e., calibrate) the individual Segments in the Stretch to the Stretch Left Edge and the Target
	 *  Constraints.
	 * 
	 * @param srvcLeading Stretch Left-most Segment Response Value Constraint
	 * @param aSRVC Array of Segment Response Value Constraints
	 * @param sbfr Stretch Fitness Weighted Response
	 * @param bs The Calibration Boundary Condition
	 * @param iCalibrationDetail The Calibration Detail
	 * 
	 * @return TRUE - Set up was successful
	 */

	public abstract boolean setup (
		final org.drip.spline.params.SegmentResponseValueConstraint srvcLeading,
		final org.drip.spline.params.SegmentResponseValueConstraint[] aSRVC,
		final org.drip.spline.params.StretchBestFitResponse sbfr,
		final org.drip.spline.stretch.BoundarySettings bs,
		final int iCalibrationDetail);

	/**
	 * Set up (i.e., calibrate) the individual Segments in the Stretch to the Stretch Left Edge Response and
	 * 	the Target Constraints.
	 * 
	 * @param dblStretchLeftResponseValue Stretch Left-most Response Value
	 * @param aSRVC Array of Segment Response Value Constraints
	 * @param sbfr Stretch Best Fit Weighted Response Values
	 * @param bs The Calibration Boundary Condition
	 * @param iCalibrationDetail The Calibration Detail
	 * 
	 * @return TRUE - Set up was successful
	 */

	public abstract boolean setup (
		final double dblStretchLeftResponseValue,
		final org.drip.spline.params.SegmentResponseValueConstraint[] aSRVC,
		final org.drip.spline.params.StretchBestFitResponse sbfr,
		final org.drip.spline.stretch.BoundarySettings bs,
		final int iCalibrationDetail);

	/**
	 * Generate a new Stretch by clipping all the Segments to the Left of the specified Predictor Ordinate.
	 *  Smoothness Constraints will be maintained.
	 * 
	 * @param strName Name of the Clipped Stretch 
	 * @param dblPredictorOrdinate Predictor Ordinate Left of which the Clipping is to be applied
	 * 
	 * @return The Clipped Stretch
	 */

	public abstract MultiSegmentSequence clipLeft (
		final java.lang.String strName,
		final double dblPredictorOrdinate);

	/**
	 * Generate a new Stretch by clipping all the Segments to the Right of the specified Predictor Ordinate.
	 * 	Smoothness Constraints will be maintained.
	 * 
	 * @param strName Name of the Clipped Stretch 
	 * @param dblPredictorOrdinate Predictor Ordinate Right of which the Clipping is to be applied
	 * 
	 * @return The Clipped Stretch
	 */

	public abstract MultiSegmentSequence clipRight (
		final java.lang.String strName,
		final double dblPredictorOrdinate);

	/**
	 * Retrieve the Span Curvature DPE
	 * 
	 * @return The Span Curvature DPE
	 * 
	 * @throws java.lang.Exception Thrown if the Span Curvature DPE cannot be computed
	 */

	public abstract double curvatureDPE()
		throws java.lang.Exception;

	/**
	 * Retrieve the Span Length DPE
	 * 
	 * @return The Span Length DPE
	 * 
	 * @throws java.lang.Exception Thrown if the Span Length DPE cannot be computed
	 */

	public abstract double lengthDPE()
		throws java.lang.Exception;

	/**
	 * Retrieve the Stretch Best Fit DPE
	 * 
	 * @param sbfr Stretch Best Fit Weighted Response Values
	 * 
	 * @return The Stretch Best Fit DPE
	 * 
	 * @throws java.lang.Exception Thrown if the Stretch Best Fit DPE cannot be computed
	 */

	public abstract double bestFitDPE (
		final org.drip.spline.params.StretchBestFitResponse sbfr)
		throws java.lang.Exception;

	/**
	 * Retrieve the Merge Stretch Manager if it exists.
	 * 
	 * @return The Merge Stretch Manager
	 */

	public org.drip.state.representation.MergeSubStretchManager msm();

	/**
	 * Display the Segments
	 * 
	 * @return The Segements String
	 */

	public abstract java.lang.String displayString();
}
