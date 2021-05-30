package com.teraculus.lingojournalandroid.ui.stats

//@Composable
//fun BarChart(values: Map<String, Float>, formatter: (Float) -> String = { it.toString() }) {
//    val YValueCount = 7
//    val points = values.toList().mapIndexed { index, pair ->
//        if ((index + 1) % YValueCount  == 0)
//            pair
//        else Pair("", pair.second)
//    }
//    val height = with(LocalDensity.current) { 100.dp.toPx().toInt() }
//    val lineThickness = with(LocalDensity.current) { 12.dp.toPx() }
//    val lineColor = MaterialTheme.colors.secondary.toArgb()
//    val labelsFont =
//        MaterialTheme.typography.caption.fontFamily?.let { MaterialTheme.typography.caption.fontStyle?.ordinal?.let { it1 ->
//            android.graphics.Typeface.create(it.toString(),
//                it1)
//        } }
//    val labelColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium).toArgb()
//    val fontSize = with(LocalDensity.current) { 10.dp.toPx() }
//    var width by remember { mutableStateOf(0) }
//
//    BoxWithConstraints {
//        if(width != constraints.maxWidth)
//            width =  constraints.maxWidth
//    }
//    key(values.size) {
////        AndroidView(::BarChartView , modifier = Modifier
////            .fillMaxWidth()
////            .height(100.dp),
////            update = { view ->
////                view.barRadius = lineThickness / 2
////                view.barsColor = lineColor
////                view.axis = axisType
////                view.labelsSize = fontSize
////                view.labelsColor = labelColor
////                view.labelsFormatter = formatter
////                if(scale != null)
////                    view.scale = scale
////                labelsFont?.let { view.labelsFont = it }
////                view.measure(width, height)
////                view.show(points)
////            })
//    }
//}

//@Composable
//fun LineChart(values: Map<String, Float>, formatter: (Float) -> String = { it.toString() }, ) {
//    val xValueCount = 7
//    val points = values.toList().mapIndexed { index, pair ->
//        if ((index + 1) % xValueCount  == 0)
//            pair
//        else Pair("", pair.second)
//    }
//    val height = with(LocalDensity.current) { 100.dp.toPx().toInt() }
//    val lineThickness = with(LocalDensity.current) { 4.dp.toPx() }
//    val lineColor = MaterialTheme.colors.secondary.toArgb()
//    val labelsFont =
//        MaterialTheme.typography.caption.fontFamily?.let { MaterialTheme.typography.caption.fontStyle?.ordinal?.let { it1 ->
//            android.graphics.Typeface.create(it.toString(),
//                it1)
//        } }
//    val labelColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium).toArgb()
//    val fontSize = with(LocalDensity.current) { 8.dp.toPx() }
//    var width by remember { mutableStateOf(0) }
//
//    BoxWithConstraints {
//        if(width != constraints.maxWidth)
//            width =  constraints.maxWidth
//    }
//    key(values.size) {
////        AndroidView(::LineChartView , modifier = Modifier
////            .fillMaxWidth()
////            .height(100.dp),
////            update = { view ->
////                view.smooth = false
////                view.lineThickness = lineThickness
////                view.lineColor = lineColor
////                //view.pointsDrawableRes = R.drawable.ic_chart_point
////                view.axis = axisType
////                view.labelsSize = fontSize
////                view.labelsColor = labelColor
////                view.labelsFormatter = formatter
////                if(scale != null)
////                    view.scale = scale
////                labelsFont?.let { view.labelsFont = it }
////                view.measure(width, height)
////                view.show(points)
////            })
//    }
//}
//
//
//@Composable
//fun SentimentBarChartScale() {
//    Column(modifier = Modifier.height(95.dp).padding(end=8.dp), verticalArrangement = Arrangement.SpaceBetween) {
//        SentimentIcon(value = 100f, modifier = Modifier.size(18.dp), color= Constants.ItemBackground)
//        SentimentIcon(value = 0f, modifier = Modifier.size(18.dp), color= Constants.ItemBackground)
//    }
//}
//
//@Composable
//fun SentimentChartsCard(stats: LanguageStatData) {
//    Column {
//        ApplyTextStyle(textStyle = MaterialTheme.typography.caption,
//            contentAlpha = ContentAlpha.medium) {
//            Text(text = "Confidence",
//                modifier = Modifier.padding(start = 16.dp, top = 8.dp))
//        }
//        Card(elevation = 2.dp, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
//            Row(modifier = Modifier.padding(16.dp)) {
//                SentimentBarChartScale()
//                LineChart(values = stats.confidencePerRangeStats, )
//            }
//        }
//
//        ApplyTextStyle(textStyle = MaterialTheme.typography.caption,
//            contentAlpha = ContentAlpha.medium) {
//            Text(text = "Motivation",
//                modifier = Modifier.padding(start = 16.dp, top = 8.dp))
//        }
//        Card(elevation = 2.dp, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
//            Row(modifier = Modifier.padding(16.dp)) {
//                SentimentBarChartScale()
//                LineChart(values = stats.motivationPerRangeStats,)
//            }
//        }
//    }
//}