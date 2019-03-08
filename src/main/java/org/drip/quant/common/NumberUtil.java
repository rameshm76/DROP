
package org.drip.quant.common;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2019 Lakshmi Krishnamurthy
 * Copyright (C) 2018 Lakshmi Krishnamurthy
 * Copyright (C) 2017 Lakshmi Krishnamurthy
 * Copyright (C) 2016 Lakshmi Krishnamurthy
 * Copyright (C) 2015 Lakshmi Krishnamurthy
 * Copyright (C) 2014 Lakshmi Krishnamurthy
 * Copyright (C) 2013 Lakshmi Krishnamurthy
 * Copyright (C) 2012 Lakshmi Krishnamurthy
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
 * <i>NumberUtil</i> implements number utility functions. It exposes the following functions:
 * 
 * <br><br>
 *  <ul>
 *  	<li>
 *			Verify number/number array validity, and closeness/sign match
 *  	</li>
 *  	<li>
 *			Factorial Permutation/Combination functionality
 *  	</li>
 *  	<li>
 *			Dump multi-dimensional array contents
 *  	</li>
 *  	<li>
 *			Min/Max/Bound the array entries within limits
 *  	</li>
 *  </ul>
 * 
 * <br><br>
 *  <ul>
 *		<li><b>Module </b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/NumericalCore.md">Numerical Core Module</a></li>
 *		<li><b>Library</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/NumericalOptimizerLibrary.md">Numerical Optimizer Library</a></li>
 *		<li><b>Project</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/quant">Quant</a></li>
 *		<li><b>Package</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/quant/common">Common</a></li>
 *  </ul>
 * <br><br>
 * 
 * @author Lakshmi Krishnamurthy
 */

public class NumberUtil {
	private static final double DEFAULT_ABSOLUTE_TOLERANCE = 1.0e-03;
	private static final double DEFAULT_RELATIVE_TOLERANCE = 1.0e-03;

	/**
	 * Check if the Input Long is MIN_VALUE or MAX_VALUE
	 * 
	 * @param l Input Long
	 * 
	 * @return TRUE - Input Long is MIN_VALUE or MAX_VALUE
	 */

	public static final boolean IsValid (
		final long l)
	{
		return java.lang.Long.MIN_VALUE != l && java.lang.Long.MAX_VALUE != l;
	}

	/**
	 * Check if the Input Long Array contains a MIN_VALUE or MAX_VALUE
	 * 
	 * @param al Input Long Array
	 * 
	 * @return TRUE - Input Long Array contains a MIN_VALUE or MAX_VALUE
	 */

	public static final boolean IsValid (
		final long[] al)
	{
		if (null == al) return true;

		for (int i = 0; i < al.length; ++i) {
			if (!IsValid (al[i])) return false;
		}

		return true;
	}

	/**
	 * Checks if the input double is Infinite or NaN
	 * 
	 * @param dbl Input double
	 * 
	 * @return TRUE - Input double is Infinite or NaN
	 */

	public static final boolean IsValid (
		final double dbl)
	{
		return !java.lang.Double.isNaN (dbl) && !java.lang.Double.isInfinite (dbl);
	}

	/**
	 * Checks if the input double array contains an Infinite or an NaN
	 * 
	 * @param adbl Input double array
	 * 
	 * @return TRUE - Input double contains an Infinite or an NaN
	 */

	public static final boolean IsValid (
		final double[] adbl)
	{
		if (null == adbl) return true;

		for (int i = 0; i < adbl.length; ++i) {
			if (!IsValid (adbl[i])) return false;
		}

		return true;
	}

	/**
	 * Compare and checks if the two input numbers fall within a specified tolerance
	 * 
	 * @param dbl1 Number #1
	 * @param dbl2 Number #2
	 * @param dblAbsoluteTolerance Absolute Tolerance
	 * @param dblRelativeTolerance Relative Tolerance
	 * 
	 * @return TRUE if they fall within the tolerance
	 */

	public static final boolean WithinTolerance (
		final double dbl1,
		final double dbl2,
		final double dblAbsoluteTolerance,
		final double dblRelativeTolerance)
	{
		if (!IsValid (dbl1) || !IsValid (dbl2)) return false;

		if (dblAbsoluteTolerance >= java.lang.Math.abs (dbl1)) {
			if (dblAbsoluteTolerance >= java.lang.Math.abs (dbl2)) return true;

			return false;
		}

		if (dblRelativeTolerance >= java.lang.Math.abs ((dbl2 - dbl1) / dbl1)) return true;

		return false;
	}

	/**
	 * Compare and checks if the two input numbers fall within a specified tolerance
	 * 
	 * @param dbl1 Number #1
	 * @param dbl2 Number #2
	 * 
	 * @return TRUE if they fall within the tolerance
	 */

	public static final boolean WithinTolerance (
		final double dbl1,
		final double dbl2)
	{
		return WithinTolerance (dbl1, dbl2, DEFAULT_ABSOLUTE_TOLERANCE, DEFAULT_RELATIVE_TOLERANCE);
	}

	/**
	 * This function implements Factorial N.
	 * 
	 * @param n N
	 * 
	 * @return Factorial N
	 */

	public static final int Factorial (
		final int n)
	{
		int iNFact = 1;

		for (int i = 1; i <= n; ++i)
			iNFact *= i;

		return iNFact;
	}

	/**
	 * This function implements N Permute K.
	 * 
	 * @param n N
	 * @param k K
	 * 
	 * @return N Permute K
	 */

	public static final int NPK (
		final int n,
		final int k)
	{
		int iK = n < k ? n : k;
		int iN = n > k ? n : k;

		return Factorial (iN) / Factorial (iK);
	}

	/**
	 * This function implements N choose K.
	 * 
	 * @param n N
	 * @param k K
	 * 
	 * @return N choose K
	 */

	public static final int NCK (
		final int n,
		final int k)
	{
		int iK = n < k ? n : k;
		int iN = n > k ? n : k;

		return Factorial (iN) / Factorial (iK) / Factorial (iN - iK);
	}

	/**
	 * Bound the input to within (floor, Ceiling), i.e., compute Min (Max (floor, X), Ceiling)
	 * 
	 * @param dblX Input Number
	 * @param dblFloor Floor
	 * @param dblCeiling Ceiling
	 * 
	 * @return Min (Max (floor, X), Ceiling)
	 * 
	 * @throws java.lang.Exception Thrown if the inputs are invalid
	 */

	public static final double Bound (
		final double dblX,
		final double dblFloor,
		final double dblCeiling)
		throws java.lang.Exception
	{
		if (!IsValid (dblX) || !IsValid (dblFloor)|| !IsValid (dblCeiling) || dblFloor > dblCeiling)
			throw new java.lang.Exception ("NumberUtil::Bound => Invalid Inputs");

		double dblBound = dblX < dblFloor ? dblFloor : dblX;
		return dblBound > dblCeiling ? dblCeiling : dblBound;
	}

	/**
	 * Retrieve the Minimum Element in the specified Array
	 * 
	 * @param adbl Array of elements
	 * 
	 * @return The Minimum Element
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are invalid
	 */

	public static final double Minimum (
		final double[] adbl)
		throws java.lang.Exception
	{
		if (!IsValid (adbl)) throw new java.lang.Exception ("NumberUtil::Minimum => Invalid Inputs");

		double dblMinimum = adbl[0];
		int iNumElement = adbl.length;

		for (int i = 1; i < iNumElement; ++i)
			dblMinimum = dblMinimum < adbl[i] ? dblMinimum : adbl[i];

		return dblMinimum;
	}

	/**
	 * Retrieve the Maximum Element in the specified Array
	 * 
	 * @param adbl Array of elements
	 * 
	 * @return The Maximum Element
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are invalid
	 */

	public static final double Maximum (
		final double[] adbl)
		throws java.lang.Exception
	{
		if (!IsValid (adbl)) throw new java.lang.Exception ("NumberUtil::Maximum => Invalid Inputs");

		double dblMaximum = adbl[0];
		int iNumElement = adbl.length;

		for (int i = 1; i < iNumElement; ++i)
			dblMaximum = dblMaximum > adbl[i] ? dblMaximum : adbl[i];

		return dblMaximum;
	}

	/**
	 * Check if the specified array contains elements all of the same sign
	 * 
	 * @param adbl Array of elements
	 * 
	 * @return TRUE - Same Sign
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are invalid
	 */

	public static final boolean SameSign (
		final double[] adbl)
		throws java.lang.Exception
	{
		if (!IsValid (adbl)) throw new java.lang.Exception ("NumberUtil::SameSign => Invalid Inputs");

		if (adbl[0] <= 0.) return false;

		int iNumElement = adbl.length;

		for (int i = 1; i < iNumElement; ++i) {
			if (adbl[0] * adbl[i] <= 0.) return false;
		}

		return true;
	}

	/**
	 * Print the contents of the 1D array
	 * 
	 * @param strName Label Name
	 * @param adblA The 1D array
	 * @param bBailOnNaN Bail on encountering an NaN
	 * 
	 * @return TRUE - Print Successful
	 */

	public static final boolean Print1DArray (
		final java.lang.String strName,
		final double[] adblA,
		final boolean bBailOnNaN)
	{
		if (null == adblA || 0 == adblA.length) return false;

		int iSize = adblA.length;

		for (int i = 0; i < iSize; ++i) {
			if (!org.drip.quant.common.NumberUtil.IsValid (adblA[i]) && bBailOnNaN) return false;

			System.out.println (strName + "[" + i + "] = " + adblA[i]);
		}

		return true;
	}

	/**
	 * Print the contents of the 1D array to the Specified Decimal Location
	 * 
	 * @param strName Label Name
	 * @param adblA The 1D array
	 * @param iNumDecimal Number of Decimal Places to Display
	 * @param bBailOnNaN Bail on encountering an NaN
	 * 
	 * @return TRUE - Print Successful
	 */

	public static final boolean Print1DArray (
		final java.lang.String strName,
		final double[] adblA,
		final int iNumDecimal,
		final boolean bBailOnNaN)
	{
		if (null == adblA || 0 == adblA.length) return false;

		int iSize = adblA.length;

		for (int i = 0; i < iSize; ++i) {
			if (!org.drip.quant.common.NumberUtil.IsValid (adblA[i]) && bBailOnNaN) return false;

			System.out.println (strName + "[" + i + "] = " + org.drip.quant.common.FormatUtil.FormatDouble
				(adblA[i], 1, iNumDecimal, 1.));
		}

		return true;
	}

	/**
	 * Print the contents of the 2D array
	 * 
	 * @param strName Label Name
	 * @param aadblA The 2D array
	 * @param bBailOnNaN Bail on encountering an NaN
	 * 
	 * @return TRUE - Print Successful
	 */

	public static final boolean Print2DArray (
		final java.lang.String strName,
		final double[][] aadblA,
		final boolean bBailOnNaN)
	{
		if (null == aadblA) return false;

		int iRowSize = aadblA.length;

		if (0 == iRowSize || null == aadblA[0]) return false;

		int iColSize = aadblA[0].length;

		if (0 == iColSize) return false;

		for (int i = 0; i < iRowSize; ++i) {
			for (int j = 0; j < iColSize; ++j) {
				if (!org.drip.quant.common.NumberUtil.IsValid (aadblA[i][j]) && bBailOnNaN) return false;

				System.out.println (strName + "[" + i + "][" + j + "] = " +
					org.drip.quant.common.FormatUtil.FormatDouble (aadblA[i][j], 1, 6, 1.));
			}
		}

		return true;
	}

	/**
	 * Print the Contents of the 2D Array Pair
	 * 
	 * @param strLeftLabel Left Label
	 * @param strRightLabel Right Label
	 * @param aadblLeft The Left 2D array
	 * @param aadblRight The Right 2D array
	 * @param bBailOnNaN Bail on encountering an NaN
	 * 
	 * @return TRUE - Print Successful
	 */

	public static final boolean Print2DArrayPair (
		final java.lang.String strLeftLabel,
		final java.lang.String strRightLabel,
		final double[][] aadblLeft,
		final double[][] aadblRight,
		final boolean bBailOnNaN)
	{
		if (null == aadblLeft || null == aadblRight) return false;

		int iSize = aadblLeft.length;

		if (0 == iSize || iSize != aadblRight.length) return false;

		for (int i = 0; i < iSize; ++i) {
			for (int j = 0; j < iSize; ++j) {
				if (!org.drip.quant.common.NumberUtil.IsValid (aadblLeft[i][j]) &&
					!org.drip.quant.common.NumberUtil.IsValid (aadblRight[i][j]) && bBailOnNaN)
					return false;

				System.out.println (strLeftLabel + "[" + i + "][" + j + "] = " +
					org.drip.quant.common.FormatUtil.FormatDouble (aadblLeft[i][j], 1, 6, 1.) + "  |  " +
						strRightLabel + "[" + i + "][" + j + "] = " +
							org.drip.quant.common.FormatUtil.FormatDouble (aadblRight[i][j], 1, 6, 1.));
			}
		}

		return true;
	}

	/**
	 * Print the Contents of the 2D Array Triplet
	 * 
	 * @param strLeftLabel Left Label
	 * @param strMiddleLabel Middle Label
	 * @param strRightLabel Right Label
	 * @param aadblLeft The Left 2D array
	 * @param aadblMiddle The Middle 2D array
	 * @param aadblRight The Right 2D array
	 * @param bBailOnNaN Bail on encountering an NaN
	 * 
	 * @return TRUE - Print Successful
	 */

	public static final boolean Print2DArrayTriplet (
		final java.lang.String strLeftLabel,
		final java.lang.String strMiddleLabel,
		final java.lang.String strRightLabel,
		final double[][] aadblLeft,
		final double[][] aadblMiddle,
		final double[][] aadblRight,
		final boolean bBailOnNaN)
	{
		if (null == aadblLeft || null == aadblMiddle || null == aadblRight) return false;

		int iSize = aadblLeft.length;

		if (0 == iSize || iSize != aadblMiddle.length || iSize != aadblRight.length) return false;

		for (int i = 0; i < iSize; ++i) {
			for (int j = 0; j < iSize; ++j) {
				if (!org.drip.quant.common.NumberUtil.IsValid (aadblLeft[i][j]) &&
						!org.drip.quant.common.NumberUtil.IsValid (aadblLeft[i][j]) &&
							!org.drip.quant.common.NumberUtil.IsValid (aadblRight[i][j]) && bBailOnNaN)
					return false;

				System.out.println (strLeftLabel + "[" + i + "][" + j + "] = " +
					org.drip.quant.common.FormatUtil.FormatDouble (aadblLeft[i][j], 1, 6, 1.) + "  |  " +
						strMiddleLabel + "[" + i + "][" + j + "] = " +
							org.drip.quant.common.FormatUtil.FormatDouble (aadblMiddle[i][j], 1, 6, 1.) +
								"  |  " + strRightLabel + "[" + i + "][" + j + "] = " +
									org.drip.quant.common.FormatUtil.FormatDouble (aadblRight[i][j], 1, 6,
										1.));
			}
		}

		return true;
	}

	/**
	 * Print the Matrix Contents
	 * 
	 * @param strName Name of the Matrix
	 * @param aadblA Matrix
	 * 
	 * @return TRUE - Matrix Contents Successfully printed
	 */

	public static final boolean PrintMatrix (
		final java.lang.String strName,
		final double[][] aadblA)
	{
		if (null == aadblA || 0 == aadblA.length) return false;

		int iSize = aadblA.length;

		for (int i = 0; i < iSize; ++i) {
			java.lang.String strDump = strName  + " => ";

			for (int j = 0; j < iSize; ++j)
				strDump += org.drip.quant.common.FormatUtil.FormatDouble (aadblA[i][j], 1, 6, 1.) + " |";

			System.out.println (strDump);
		}

		return true;
	}

	/**
	 * Compute (n - 0.5)!
	 * 
	 * @param n n
	 * 
	 * @return (n - 0.5)! Value
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public static final double HalfDownShiftedFactorial (
		final int n)
		throws java.lang.Exception
	{
		if (-1 >= n)
		{
			throw new java.lang.Exception ("NumberUtil::HalfDownShiftedFactorial => Invalid Inputs");
		}

		double halfDownShiftedFactorial = java.lang.Math.sqrt (java.lang.Math.PI);

		for (double index = 1; index < n; ++index)
		{
			halfDownShiftedFactorial = halfDownShiftedFactorial * (index + 0.5);
		}

		return halfDownShiftedFactorial;
	}
}
