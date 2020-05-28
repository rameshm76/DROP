
package org.drip.graph.shortestpath;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2020 Lakshmi Krishnamurthy
 * 
 *  This file is part of DROP, an open-source library targeting analytics/risk, transaction cost analytics,
 *  	asset liability management analytics, capital, exposure, and margin analytics, valuation adjustment
 *  	analytics, and portfolio construction analytics within and across fixed income, credit, commodity,
 *  	equity, FX, and structured products. It also includes auxiliary libraries for algorithm support,
 *  	numerical analysis, numerical optimization, spline builder, model validation, statistical learning,
 *  	and computational support.
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
 * <i>YenVertexScanOptimizer</i> optimizes the Shortest Path Generation for a Directed Graph under the
 * 	Bellman-Ford Algorithm. This happens by eliminating unnecessary Vertex Scans. The References are:
 * 
 * <br><br>
 *  <ul>
 *  	<li>
 *  		Bang-Jensen, J., and G. Gutin (2008): <i>Digraphs: Theory, Algorithms, and Applications
 *  			2<sup>nd</sup> Edition</i> <b>Springer</b>
 *  	</li>
 *  	<li>
 *  		Cormen, T., C. E. Leiserson, R. Rivest, and C. Stein (2009): <i>Introduction to Algorithms</i>
 *  			3<sup>rd</sup> Edition <b>MIT Press</b>
 *  	</li>
 *  	<li>
 *  		Kleinberg, J., and E. Tardos (2022): <i>Algorithm Design 2<sup>nd</sup> Edition</i> <b>Pearson</b>
 *  	</li>
 *  	<li>
 *  		Sedgewick, R. and K. Wayne (2011): <i>Algorithms 4<sup>th</sup> Edition</i> <b>Addison Wesley</b>
 *  	</li>
 *  	<li>
 *  		Wikipedia (2020): Bellman-Ford Algorithm
 *  			https://en.wikipedia.org/wiki/Bellman%E2%80%93Ford_algorithm
 *  	</li>
 *  </ul>
 *
 * <br><br>
 *  <ul>
 *		<li><b>Module </b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></li>
 *		<li><b>Library</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/GraphAlgorithmLibrary.md">Graph Algorithm Library</a></li>
 *		<li><b>Project</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/graph/README.md">Graph Optimization and Tree Construction Algorithms</a></li>
 *		<li><b>Package</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/graph/shortestpath/README.md">Shortest Path Generation Algorithm Family</a></li>
 *  </ul>
 * <br><br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class YenVertexScanOptimizer
{
	private java.util.Map<java.lang.String, java.lang.Boolean> _vertexScanMap = null;
	private java.util.Map<java.lang.String, java.lang.Double> _vertexDistanceMap = null;

	/**
	 * YenVertexScanOptimizer Constructor
	 * 
	 * @param augmentedVertexMap The Augmented Vertex Map
	 * 
	 * @throws java.lang.Exception Thrown if the Input is Invalid
	 */

	public YenVertexScanOptimizer (
		final java.util.Map<java.lang.String, org.drip.graph.shortestpath.AugmentedVertex>
			augmentedVertexMap)
		throws java.lang.Exception
	{
		if (null == augmentedVertexMap || 0 == augmentedVertexMap.size())
		{
			throw new java.lang.Exception (
				"YenVertexScanOptimizer Constructor => Invalid Inputs"
			);
		}

		_vertexScanMap = new org.drip.analytics.support.CaseInsensitiveHashMap<java.lang.Boolean>();

		_vertexDistanceMap = new org.drip.analytics.support.CaseInsensitiveHashMap<java.lang.Double>();

		for (java.util.Map.Entry<java.lang.String, org.drip.graph.shortestpath.AugmentedVertex>
			augmentedVertexEntry : augmentedVertexMap.entrySet())
		{
			java.lang.String vertexName = augmentedVertexEntry.getKey();

			_vertexScanMap.put (
				vertexName,
				true
			);

			_vertexDistanceMap.put (
				vertexName,
				augmentedVertexEntry.getValue().weight()
			);
		}
	}

	/**
	 * Retrieve the Vertex Scan Map
	 * 
	 * @return The Vertex Scan Map
	 */

	public java.util.Map<java.lang.String, java.lang.Boolean> vertexScanMap()
	{
		return _vertexScanMap;
	}

	/**
	 * Retrieve the Vertex Distance Map
	 * 
	 * @return The Vertex Distance Map
	 */

	public java.util.Map<java.lang.String, java.lang.Double> vertexDistanceMap()
	{
		return _vertexDistanceMap;
	}

	/**
	 * Scan and Update the Vertexes
	 * 
	 * @param updatedAugmentedVertexMap The Updated Augmented Vertex Map
	 * 
	 * @return TRUE - The Vertexes are Scanned and Updated
	 */

	public boolean scanAndUpdateVertexes (
		final java.util.Map<java.lang.String, org.drip.graph.shortestpath.AugmentedVertex>
			updatedAugmentedVertexMap)
	{
		if (null == updatedAugmentedVertexMap || updatedAugmentedVertexMap.size() != _vertexScanMap.size())
		{
			return false;
		}

		for (java.util.Map.Entry<java.lang.String, org.drip.graph.shortestpath.AugmentedVertex>
			updatedAugmentedVertexEntry : updatedAugmentedVertexMap.entrySet())
		{
			java.lang.String vertexName = updatedAugmentedVertexEntry.getKey();

			double updatedVertexDistance = updatedAugmentedVertexEntry.getValue().weight();

			if (_vertexDistanceMap.get (
					vertexName
				) == updatedVertexDistance
			)
			{
				_vertexScanMap.put (
					vertexName,
					false
				);
			}
			else
			{
				_vertexDistanceMap.put (
					vertexName,
					updatedVertexDistance
				);
			}
		}

		return true;
	}

	/**
	 * Indicate if the Vertex Needs a Scan
	 * 
	 * @param vertexName The Vertex Name
	 * 
	 * @return TRUE - The Vertex Needs a Scan
	 */

	public boolean vertexNeedsScan (
		final java.lang.String vertexName)
	{
		if (null == vertexName || vertexName.isEmpty() ||
			!_vertexScanMap.containsKey (
				vertexName
			)
		)
		{
			return false;
		}

		return _vertexScanMap.get (
			vertexName
		);
	}
}
