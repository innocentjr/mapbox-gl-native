#include <mbgl/style/expression/collator.hpp>
#include <mbgl/style/expression/collator_expression.hpp>
#include <mbgl/style/expression/literal.hpp>
#include <mbgl/util/string.hpp>

namespace mbgl {
namespace style {
namespace expression {
    
CollatorExpression::CollatorExpression(std::unique_ptr<Expression> caseSensitive_,
                   std::unique_ptr<Expression> diacriticSensitive_,
                   std::unique_ptr<Expression> locale_)
    : Expression(type::Collator)
    , caseSensitive(std::move(caseSensitive_))
    , diacriticSensitive(std::move(diacriticSensitive_))
    , locale(std::move(locale_))
{}

using namespace mbgl::style::conversion;

ParseResult CollatorExpression::parse(const Convertible& value, ParsingContext& ctx) {
    if (arrayLength(value) != 2) {
        ctx.error("Expected one argument.");
        return ParseResult();
    }

    auto options = arrayMember(value, 1);
    if (!isObject(options)) {
        ctx.error("CollatorExpression options argument must be an object.");
        return ParseResult();
    }
    
    const optional<Convertible> caseSensitiveOption = objectMember(value, "case-sensitive");
    ParseResult caseSensitive;
    if (caseSensitiveOption) {
        caseSensitive = ctx.parse(*caseSensitiveOption, 1, {type::Boolean});
    } else {
        caseSensitive = { std::make_unique<Literal>(false) };
    }
    if (!caseSensitive) {
        return ParseResult();
    }

    const optional<Convertible> diacriticSensitiveOption = objectMember(value, "diacritic-sensitive");
    ParseResult diacriticSensitive;
    if (diacriticSensitiveOption) {
        diacriticSensitive = ctx.parse(*diacriticSensitiveOption, 1, {type::Boolean});
    } else {
        diacriticSensitive = { std::make_unique<Literal>(false) };
    }
    if (!diacriticSensitive) {
        return ParseResult();
    }
    
    const optional<Convertible> localeOption = objectMember(value, "locale");
    ParseResult locale;
    if (localeOption) {
        locale = ctx.parse(*localeOption, 1, {type::String});
        if (!locale) {
            return ParseResult();
        }
    }
    
    std::unique_ptr<Expression> localePtr;
    if (locale) {
        localePtr = std::move(*locale);
    }
    return ParseResult(std::make_unique<CollatorExpression>(std::move(*caseSensitive), std::move(*diacriticSensitive), std::move(localePtr)));
}
    
void CollatorExpression::eachChild(const std::function<void(const Expression&)>& fn) const {
    fn(*caseSensitive);
    fn(*diacriticSensitive);
    if (locale.get()) {
        fn(*locale);
    }
}
    
bool CollatorExpression::operator==(const Expression& e) const {
    if (auto rhs = dynamic_cast<const CollatorExpression*>(&e)) {
        if ((locale.get() && (!rhs->locale.get() || *locale != *(rhs->locale))) ||
            (!locale.get() && rhs->locale.get())) {
            return false;
        }
        return *caseSensitive == *(rhs->caseSensitive) &&
            *diacriticSensitive == *(rhs->diacriticSensitive);
    }
    return false;
}

mbgl::Value CollatorExpression::serialize() const {
    std::unordered_map<std::string, mbgl::Value> result;
    result["case-sensitive"] = caseSensitive->serialize();
    result["diacritic-sensitive"] = diacriticSensitive->serialize();
    if (locale.get()) {
        result["locale"] = locale->serialize();
    }
    return result;
}
    
EvaluationResult CollatorExpression::evaluate(const EvaluationContext& params) const {
    auto caseSensitiveResult = caseSensitive->evaluate(params);
    if (!caseSensitiveResult) {
        return caseSensitiveResult.error();
    }
    auto diacriticSensitiveResult = diacriticSensitive->evaluate(params);
    if (!diacriticSensitiveResult) {
        return diacriticSensitiveResult.error();
    }
    
    if (locale.get()) {
        auto localeResult = locale->evaluate(params);
        if (!localeResult) {
            return localeResult.error();
        }
        return Collator(caseSensitiveResult->get<bool>(), diacriticSensitiveResult->get<bool>(), localeResult->get<std::string>());
    } else {
        return Collator(caseSensitiveResult->get<bool>(), diacriticSensitiveResult->get<bool>());
    }
}

} // namespace expression
} // namespace style
} // namespace mbgl
