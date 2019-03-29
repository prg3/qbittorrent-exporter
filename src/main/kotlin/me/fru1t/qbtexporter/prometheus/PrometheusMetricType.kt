package me.fru1t.qbtexporter.prometheus

/**
 * The various types of metrics that prometheus accepts. See
 * [official documentation](https://prometheus.io/docs/concepts/metric_types/).
 */
enum class PrometheusMetricType {
  /**
   * A cumulative metrics that represents a single monotonically increasing counter whose value can
   * only increase or be reset to zero on restart. For example, you can use a counter to represent
   * the number of requests served, tasks completed, or errors.
   *
   * Do not use a counter to expose a value that can decrease. For example, do not use a counter for
   * the number of currently running processes; instead use a gauge.
   */
  COUNTER,

  /**
   * A gauge is a metric that represents a single numerical value that can arbitrarily go up and
   * down.
   *
   * Gauges are typically used for measured values like temperatures or current memory usage, but
   * also "counts" that can go up and down, like the number of concurrent requests.
   */
  GAUGE,

  /**
   * A histogram samples observations (usually things like request durations or response sizes) and
   * counts them in configurable buckets. It also provides a sum of all observed values.
   *
   * A histogram with a base metric name of <basename> exposes multiple time series during a scrape:
   *  - cumulative counters for the observation buckets, exposed as
   *    `<basename>_bucket{le="<upper inclusive bound>"}`
   *  - the total sum of all observed values, exposed as `<basename>_sum`
   *  - the count of events that have been observed, exposed as `<basename>_count` (identical to
   *    `<basename>_bucket{le="+Inf"}` above)
   *
   *  Use the `histogram_quantile()` function to calculate quantiles from histograms or even
   *  aggregations of histograms. A histogram is also suitable to calculate an Apdex score. When
   *  operating on buckets, remember that the histogram is cumulative. See
   *  [histograms and summaries](https://prometheus.io/docs/practices/histograms/) for details of
   *  histogram usage and differences to [SUMMARY].
   */
  HISTOGRAM,

  /**
   * Similar to a histogram, a summary samples observations (usually things like request durations
   * and response sizes). While it also provides a total count of observations and a sum of all
   * observed values, it calculates configurable quantiles over a sliding time window.
   *
   * A summary with a base metric name of `<basename>` exposes multiple time series during a scrape:
   *  - streaming φ-quantiles (0 ≤ φ ≤ 1) of observed events, exposed as
   *    `<basename>{quantile="<φ>"}`
   *  - the total sum of all observed values, exposed as `<basename>_sum`
   *  - the count of events that have been observed, exposed as `<basename>_count`
   *
   *  See [histograms and summaries](https://prometheus.io/docs/practices/histograms/) for detailed
   *  explanations of φ-quantiles, summary usage, and differences to [HISTOGRAM].
   */
  SUMMARY
}
