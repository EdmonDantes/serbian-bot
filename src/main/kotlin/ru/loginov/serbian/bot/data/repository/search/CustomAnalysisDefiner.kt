//package ru.loginov.serbian.bot.data.repository.search
//
//import org.apache.lucene.analysis.core.LowerCaseFilterFactory
//import org.apache.lucene.analysis.ngram.NGramFilterFactory
//import org.apache.lucene.analysis.standard.StandardTokenizerFactory
//import org.hibernate.search.backend.lucene.analysis.LuceneAnalysisConfigurationContext
//import org.hibernate.search.backend.lucene.analysis.LuceneAnalysisConfigurer
//
//class CustomAnalysisDefiner : LuceneAnalysisConfigurer {
//
//    override fun configure(context: LuceneAnalysisConfigurationContext) {
////        context.analyzer("nGramAnalyzer").custom()
////                .tokenizer(StandardTokenizerFactory::class.java)
////                .tokenFilter(LowerCaseFilterFactory::class.java)
////                .tokenFilter(NGramFilterFactory::class.java).param("maxGramSize", "1024")
//    }
//}