package function;

import fitnesse.responders.run.SuiteResponder;
import fitnesse.wiki.*;

public class FitnessExample {

    private TestableHtmlMaker testableHtmlMaker = new TestableHtmlMaker();

    public String testableHtml(PageData pageData, boolean includeSuiteSetup) throws Exception {
        StringBuffer buffer = new StringBuffer();

        if (pageData.hasAttribute("Test")) {
            testableHtmlMaker.setup(pageData.getWikiPage(), buffer, includeSuiteSetup);
        }

        buffer.append(pageData.getContent());

        if (pageData.hasAttribute("Test")) {
            testableHtmlMaker.tearDown(pageData.getWikiPage(), buffer, includeSuiteSetup);
        }

        pageData.setContent(buffer.toString());
        return pageData.getHtml();
    }

    public class TestableHtmlMaker {

        public void setup(WikiPage wikiPage, StringBuffer buffer, boolean includeSuiteSetup) throws Exception {
            if (includeSuiteSetup) {
                includeSuiteSetup(wikiPage, buffer, SuiteResponder.SUITE_SETUP_NAME, "setup");
            }
            WikiPage setup = PageCrawlerImpl.getInheritedPage("SetUp", wikiPage);
            includeSetup(wikiPage, buffer, setup);
        }

        public void tearDown(WikiPage wikiPage, StringBuffer buffer, boolean includeSuiteSetup) throws Exception {
            WikiPage teardown = PageCrawlerImpl.getInheritedPage("TearDown", wikiPage);
            testableHtmlMaker.includeTeardown(wikiPage, buffer, teardown);
            if (includeSuiteSetup) {
                testableHtmlMaker.includeSuiteSetup(wikiPage, buffer, SuiteResponder.SUITE_TEARDOWN_NAME, "teardown");
            }
        }

        public void includeSuiteSetup(WikiPage wikiPage, StringBuffer buffer, String responder, String step) throws Exception {
            WikiPage suiteSetup = PageCrawlerImpl.getInheritedPage(responder, wikiPage);
            if (suiteSetup != null) {
                WikiPagePath pagePath = wikiPage.getPageCrawler().getFullPath(suiteSetup);
                String pagePathName = PathParser.render(pagePath);
                buffer.append("!include -" + step + " .").append(pagePathName).append("\n");
            }
        }

        public void includeSetup(WikiPage wikiPage, StringBuffer buffer, WikiPage setup) throws Exception {
            if (setup != null) {
                WikiPagePath setupPath = wikiPage.getPageCrawler().getFullPath(setup);
                String setupPathName = PathParser.render(setupPath);
                buffer.append("!include -setup .").append(setupPathName).append("\n");
            }
        }

        public void includeTeardown(WikiPage wikiPage, StringBuffer buffer, WikiPage teardown) throws Exception {
            if (teardown != null) {
                WikiPagePath tearDownPath = wikiPage.getPageCrawler().getFullPath(teardown);
                String tearDownPathName = PathParser.render(tearDownPath);
                buffer.append("!include -teardown .").append(tearDownPathName).append("\n");
            }
        }
    }
}
